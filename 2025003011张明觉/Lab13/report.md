# Lab13 实验报告

## 一、实验概述

本次实验实现了一个 Bookshelf 网络书架应用，通过 Apifox Mock 接口获取图片数据，综合练习了协程、Retrofit、Gson、Coil、Repository 模式与依赖注入。

## 二、Apifox Mock 接口使用原因
改用 Apifox Mock 接口的主要原因：
1. **稳定性高**：Mock 接口提供稳定的测试数据，不受真实服务器状态影响
2. **开发效率**：无需等待后端 API 开发完成即可进行前端开发
3. **环境隔离**：避免对真实数据产生影响，便于测试和调试
4. **快速验证**：可以快速验证网络请求逻辑是否正确

## 三、Retrofit 服务接口定义
Retrofit 服务接口定义如下：
```kotlin
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

- 使用 `@GET` 注解指定 HTTP GET 请求
- 方法名为 `getBooks`，返回 `List<BookDto>` 类型
- 接口方法使用 `suspend` 修饰符，支持协程调用
- 基础 URL 配置为：`https://m1.apifoxmock.com/m1/8321477-8085280-default/`

## 四、Repository 隔离网络数据源
Repository 模式通过以下方式隔离网络数据源：
### 4.1 接口定义

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

### 4.2 网络实现

`NetworkBooksRepository` 通过 Retrofit 获取网络数据：

```kotlin
class NetworkBooksRepository(
    private val bookshelfApiService: BookshelfApiService
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return bookshelfApiService.getBooks().map { it.asExternalModel() }
    }
    // ...
}
```

### 4.3 离线兜底实现

`OfflineBooksRepository` 提供断网时的兜底数据：

```kotlin
class OfflineBooksRepository : BooksRepository {
    private val sampleBooks = listOf(
        Book("1", "https://picsum.photos/id/10/800/600", "Book 1"),
        // ...
    )
    // ...
}
```

### 4.4 依赖注入

通过 `AppContainer` 集中创建依赖：

```kotlin
class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(bookshelfApiService)
    }
}
```

**隔离优势**：
- ViewModel 不直接依赖 Retrofit，降低耦合度
- 便于替换数据源（网络/本地/测试）
- 提高代码可测试性

## 五、UI 状态管理
应用设计了三种 UI 状态：
### 5.1 状态定义

```kotlin
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}
```

### 5.2 状态切换流程

```
┌─────────────────────────────────────────────────────────────┐
│                    UI 状态切换流程                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   [初始化]                                                   │
│       │                                                     │
│       ▼                                                     │
│   [Loading] ──加载完成──▶ [Success] ──点击重试──▶ [Loading]  │
│       │                                                     │
│       └──加载失败──▶ [Error] ──点击重试──▶ [Loading]        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 状态切换实现

```kotlin
fun loadBooks() {
    viewModelScope.launch {
        _uiState.value = BookshelfUiState.Loading  // 切换到加载状态
        try {
            val books = booksRepository.getBooks()
            _uiState.value = BookshelfUiState.Success(books)  // 切换到成功状态
        } catch (e: Exception) {
            _uiState.value = BookshelfUiState.Error(e.message)  // 切换到错误状态
        }
    }
}
```

## 六、运行截图
应用运行时的界面包括：
1. **加载中状态**：显示 CircularProgressIndicator 进度指示器
2. **成功状态**：显示 LazyVerticalGrid 图片网格，每行 2 列
3. **错误状态**：显示错误图标、错误信息和重试按钮
4. **详情弹窗**：点击图书卡片后显示大图预览

## 七、遇到的问题及解决

### 7.1 问题：Retrofit 接口调用报错
**原因**：未在 AndroidManifest.xml 中添加网络权限
**解决**：添加 `<uses-permission android:name="android.permission.INTERNET" />`
### 7.2 问题：图片加载失败
**原因**：图片 URL 可能无效或网络请求失败
**解决**：
- 检查 URL 是否正确
- 使用 Coil 的 error 和 placeholder 占位符
- 确保使用 HTTPS 协议
### 7.3 问题：协程调用异常
**原因**：在非协程作用域中调用 suspend 函数
**解决**：使用 `viewModelScope.launch` 在 ViewModel 中执行协程

## 八、项目结构
```
Lab13/
├── BookshelfApplication.kt   # 应用入口，创建 AppContainer
├── Book.kt                   # 领域模型
├── BookDto.kt                # 网络数据传输对象
├── ApiConfig.kt              # API 配置常量
├── BookshelfApiService.kt    # Retrofit 服务接口
├── AppContainer.kt           # 依赖注入容器
├── BooksRepository.kt        # 数据仓库接口及实现
├── BookshelfViewModel.kt     # 视图模型
├── BookshelfScreen.kt        # Compose 界面组件
├── screenshot.png            # 运行截图
└── report.md                 # 实验报告
```

## 九、总结

通过本次实验，我掌握了以下技能：

1. 使用 Retrofit 创建网络服务接口
2. 使用 Gson 解析 JSON 响应
3. 使用 Repository 模式隔离数据层
4. 使用 Coil 加载远程图片
5. 使用 LazyVerticalGrid 构建网格界面
6. 实现 Loading/Success/Error 三种 UI 状态
7. 通过依赖注入管理数据源