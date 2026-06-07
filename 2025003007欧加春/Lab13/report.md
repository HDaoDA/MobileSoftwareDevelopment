# Lab13 实验报告：Bookshelf 网络书架应用

## 一、为什么本实验改用 Apifox Mock 接口

在真实的 Android 应用开发中，网络请求通常需要连接后端服务器。但在教学实验中，后端服务可能不可用或部署成本较高。Apifox Mock 提供了一种**无需真实后端**即可获取稳定 JSON 响应的方式：

1. **接口稳定可控**：Mock 数据由 Apifox 平台托管，返回格式固定，不会因后端变动导致前端频繁修改。
2. **降低实验门槛**：学生无需搭建服务器，只需配置 Retrofit 基础地址即可发起请求。
3. **聚焦客户端逻辑**：实验重点在于 Retrofit、Gson、Coil、Repository 等客户端技术的练习，Mock 接口让注意力集中在客户端架构设计上。
4. **HTTPS 安全**：Apifox Mock 使用 HTTPS，无需额外配置 `usesCleartextTraffic`。

---

## 二、Retrofit 服务接口如何定义

Retrofit 服务接口 `BookshelfApiService` 定义如下：

```kotlin
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

**关键设计点**：

- **`suspend` 修饰符**：将 Retrofit 调用声明为挂起函数，使其可以在协程中直接调用而无需回调或 `enqueue`，与 `viewModelScope` 配合实现异步网络请求。
- **`@GET("photos")` 注解**：声明 HTTP GET 方法，路径 `photos` 会拼接在 `BASE_URL` 之后，最终请求地址为 `https://m1.apifoxmock.com/m1/8321477-8085280-default/photos`。
- **返回类型 `List<BookDto>`**：Gson 转换器会自动将 JSON 数组反序列化为 Kotlin 列表。`BookDto` 使用 `@SerializedName("img_src")` 注解将 JSON 的 `img_src` 字段映射到 Kotlin 的 `imgSrc` 属性。

**Retrofit 实例构建**：

```kotlin
val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
```

通过 `addConverterFactory` 注册 Gson 转换器，Retrofit 自动完成 JSON → Kotlin 对象的转换。

---

## 三、Repository 如何隔离网络数据源

本实验采用**接口 + 实现**的 Repository 模式：

### 接口定义

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

### 两个实现

| 实现类 | 数据来源 | 用途 |
|--------|----------|------|
| `NetworkBooksRepository` | Retrofit 请求 Apifox Mock | 正常网络环境下的数据获取 |
| `OfflineBooksRepository` | 内存中的静态列表 | 断网时的兜底数据源 |

### 隔离原理

```
ViewModel → BooksRepository（接口）→ NetworkBooksRepository / OfflineBooksRepository
```

1. **ViewModel 只依赖 `BooksRepository` 接口**，不直接持有 Retrofit 实例或 API Service。
2. **通过 `AppContainer` 实现手动依赖注入**，在 Application 层创建具体实现并注入 ViewModel。
3. **替换数据源只需修改 `AppContainer`**，ViewModel 和 UI 层代码无需任何改动。
4. **便于单元测试**：可以轻松创建 `FakeBooksRepository` 返回预设数据，无需启动网络服务。

### 依赖注入容器

```kotlin
class DefaultAppContainer(context: Context) : AppContainer {
    private val retrofit: Retrofit by lazy { ... }
    private val retrofitService: BookshelfApiService by lazy { ... }
    override val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(retrofitService)
    }
}
```

所有依赖对象通过 `lazy` 延迟初始化，按需创建且全局单例。

---

## 四、应用的 Loading / Success / Error 状态如何切换

### 状态定义

使用 Kotlin `sealed interface` 定义三种 UI 状态：

```kotlin
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>, val selectedBook: Book? = null) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}
```

### 状态切换逻辑（ViewModel 中）

```kotlin
fun getBooks() {
    viewModelScope.launch {
        _uiState.value = BookshelfUiState.Loading    // 1. 先切换为 Loading
        try {
            val books = booksRepository.getBooks()
            _uiState.value = BookshelfUiState.Success(books)  // 2. 成功 → Success
        } catch (e: IOException) {
            _uiState.value = BookshelfUiState.Error(...)      // 3. 失败 → Error
        }
    }
}
```

**流程**：
1. **发起请求前**：将状态设为 `Loading`，UI 显示进度指示器。
2. **请求成功**：将状态设为 `Success`，携带书籍列表数据，UI 渲染图片网格。
3. **请求失败**：捕获 `IOException` 等异常，将状态设为 `Error`，UI 显示错误信息和重试按钮。
4. **用户点击重试**：再次调用 `getBooks()`，重新触发 Loading → Success/Error 流程。

### UI 层响应

```kotlin
when (val state = uiState) {
    is BookshelfUiState.Loading -> LoadingContent(...)
    is BookshelfUiState.Success -> BookshelfGridContent(...) + BookDetailDialog(...)
    is BookshelfUiState.Error -> ErrorContent(...)
}
```

使用 `StateFlow` + `collectAsState()` 让 Compose 自动响应状态变化并重组 UI。

---

## 五、运行截图

> **注意**：请在 Android Studio 中运行应用后，使用 Android Studio 截图工具或系统截图工具截取以下两张图片，保存为 `screenshot.png`：
>
> 1. **首页图片网格**：显示远程图片加载成功的书架网格界面
> 2. **详情弹窗**：点按任意书籍后弹出的详情对话框
>
> 截图请保存到 `Lab13/` 目录下的 `screenshot.png` 文件中。
>
> ⚠️ 严禁使用手机拍屏幕。

---

## 六、遇到的问题与解决

### 问题 1：图片加载失败
**原因**：Coil 默认使用 OkHttp，如果网络不通或 URL 无效，图片无法加载。
**解决**：先在浏览器中打开 `img_src` 对应的 URL 确认可访问性；确认已添加 `INTERNET` 权限。

### 问题 2：JSON 字段名与 Kotlin 属性名不一致
**原因**：API 返回 `img_src`（下划线命名），Kotlin 惯用 `imgSrc`（驼峰命名）。
**解决**：使用 `@SerializedName("img_src")` 注解进行字段映射。

### 问题 3：ViewModel 中直接使用 Retrofit
**原因**：违反分层架构原则，导致 ViewModel 与具体网络实现耦合。
**解决**：通过 Repository 接口隔离，ViewModel 只调用 Repository 的方法。

### 问题 4：断网时应用崩溃
**原因**：网络异常未被捕获，导致协程抛出未处理异常。
**解决**：在 ViewModel 的 `getBooks()` 中使用 `try-catch` 捕获 `IOException`，切换到 Error 状态并显示重试按钮。可进一步替换为 `OfflineBooksRepository` 提供兜底数据。

---

## 七、项目结构

```
app/src/main/java/com/example/bookshelf/
├── BookshelfApplication.kt    # Application 类，持有 AppContainer
├── MainActivity.kt             # 入口 Activity，设置 Compose 内容
├── data/
│   ├── AppContainer.kt         # 依赖注入容器
│   ├── BooksRepository.kt      # Repository 接口
│   ├── NetworkBooksRepository.kt  # 网络数据源实现
│   └── OfflineBooksRepository.kt  # 离线兜底数据源
├── model/
│   ├── Book.kt                 # 领域模型
│   └── BookDto.kt              # DTO 数据类 + 转换函数
├── network/
│   ├── ApiConfig.kt            # BASE_URL 常量
│   └── BookshelfApiService.kt  # Retrofit 服务接口
└── ui/
    ├── BookshelfScreen.kt      # Compose UI 界面
    └── BookshelfViewModel.kt   # ViewModel + UI 状态
```
