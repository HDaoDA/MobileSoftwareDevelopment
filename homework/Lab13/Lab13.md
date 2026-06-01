# Lab13：创建 Bookshelf 网络书架应用

## 实验背景

本实验要求应用通过 Apifox Mock 接口下载图片数据，用于综合练习协程、Retrofit、Gson、Coil、Repository 与依赖注入。

---

## 前提条件

- 已安装 Android Studio，能够创建并运行 Empty Activity 项目
- 熟悉 Kotlin 协程与 `viewModelScope`
- 了解 Compose 中 `LazyVerticalGrid`、`Card`、`AlertDialog` 等组件的基本用法
- 了解 ViewModel 与 UI state 的基本组织方式
- 已完成前面关于网络请求、状态管理、导航或自适应界面的实验

---

## 实验目标

完成本实验后，你应能够：

- 使用 Retrofit 创建网络服务接口并请求 JSON 数据
- 使用 Gson 将 JSON 响应解析为 Kotlin 数据对象
- 使用 Repository 隔离数据层，并通过依赖注入替换真实或虚构数据源
- 使用 Coil 的 `AsyncImage` 从 URL 加载图片
- 使用 `LazyVerticalGrid` 构建书架网格界面
- 为加载中、加载成功和加载失败设计清晰的 UI 状态
- 使用稳定的 Mock 数据源完成可运行的网络数据练习

---

## 本实验使用的数据源

### API 地址

```text
https://m1.apifoxmock.com/m1/8321477-8085280-default/photos
```

### 响应结构示例

```json
[
  {
    "id": "1",
    "img_src": "https://picsum.photos/id/10/800/600"
  },
  {
    "id": "2",
    "img_src": "https://picsum.photos/id/11/800/600"
  }
]
```

你需要重点使用以下字段：

| 字段 | 用途 |
|------|------|
| `id` | 作为列表 key 和详情查找标识 |
| `img_src` | 传给 Coil 加载远程图片 |

---

## 实验任务

本次实验需要完成一个可运行的 Bookshelf 应用，必须包含网络层、数据层、ViewModel 和 Compose UI。

### 任务一：创建项目并添加依赖

创建 Empty Activity 项目，包名可使用：

```text
com.example.bookshelf
```

在 `app/build.gradle.kts` 中添加网络与图片加载依赖：

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("io.coil-kt:coil-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
```

在 `AndroidManifest.xml` 中添加网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 任务二：规划数据结构

创建以下数据类型：

- `BookDto`：对应 Apifox Mock 返回的单条图片数据
- `Book`：应用内部使用的领域模型
- `asExternalModel()`：把 DTO 转换为 UI 更方便使用的模型

示例：

```kotlin
import com.google.gson.annotations.SerializedName

data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)
```

### 任务三：创建 Retrofit 服务

创建 `BookshelfApiService`：

```kotlin
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

Retrofit 基础地址：

```kotlin
const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"
```

### 任务四：实现 Repository 与依赖注入

创建 `BooksRepository` 接口：

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

然后实现：

- `NetworkBooksRepository`：通过 Retrofit 获取 Apifox Mock 图片数据
- `OfflineBooksRepository`：提供断网兜底数据，便于网络异常时运行
- `AppContainer`：集中创建 Retrofit、API service 和 Repository

Repository 的作用是让 ViewModel 不直接依赖 Retrofit，从而便于替换数据源和编写测试。

### 任务五：设计 UI 状态与 ViewModel

至少设计三类状态：

```kotlin
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(...) : BookshelfUiState
    data class Error(...) : BookshelfUiState
}
```

ViewModel 需要完成：

- 启动时加载数据列表
- 暴露 `uiState` 给 Compose 界面
- 处理重试按钮
- 点按条目时显示详情
- 关闭详情弹窗

### 任务六：构建 Compose 界面

界面至少包含：

- 顶部标题栏
- 加载中进度指示器
- 错误提示与重试按钮
- 图片网格
- 每个条目的标题或编号
- 点按条目后显示详情弹窗

图片必须使用 Coil 加载：

```kotlin
AsyncImage(
    model = book.coverUrl,
    contentDescription = book.title,
    contentScale = ContentScale.Crop
)
```

---

## 代码结构参考

```text
Lab13/
├── Lab13.md
└── basic-android-kotlin-compose-training-bookshelf/
    └── app/src/main/
        ├── java/com/example/bookshelf/
        │   ├── MainActivity.kt
        │   ├── BookshelfApplication.kt
        │   ├── data/
        │   ├── model/
        │   ├── network/
        │   └── ui/
        └── AndroidManifest.xml
```

---

## 运行检查

1. 在 Android Studio 打开：
   ```text
   Lab13/basic-android-kotlin-compose-training-bookshelf/
   ```
2. 运行到模拟器或真机。
3. 确认首页显示远程图片网格。
4. 点按任意条目，确认可以打开详情弹窗。
5. 断开网络后重新运行，应用应通过兜底数据显示界面。

---

## 提示

- Apifox Mock 是 HTTPS 接口，不需要 `usesCleartextTraffic`。
- 如果图片加载失败，先复制 `img_src` 到浏览器中检查是否能打开。
- 网络层 DTO 不要直接暴露给 UI，推荐转换为领域模型 `Book`。
- 不要在 Composable 中直接发起 Retrofit 请求，请通过 ViewModel 调用 Repository。
- 截图请使用 Android Studio 或系统截图工具，严禁使用手机拍屏幕。

---

## 提交要求

在自己的文件夹下新建 `Lab13/` 目录，只提交本实验最关键的源码、配置、截图和报告。文件名必须保持一致，便于批改时快速定位。

```text
学号姓名/
└── Lab13/
    ├── BookshelfApplication.kt
    ├── Book.kt
    ├── BookDto.kt
    ├── BookshelfApiService.kt
    ├── ApiConfig.kt
    ├── AppContainer.kt
    ├── BooksRepository.kt
    ├── BookshelfViewModel.kt
    ├── BookshelfScreen.kt
    ├── screenshot.png
    └── report.md
```

报告中需要说明：

- 为什么本实验改用 Apifox Mock 接口
- Retrofit 服务接口如何定义
- Repository 如何隔离网络数据源
- 应用的 Loading / Success / Error 状态如何切换
- 运行截图和遇到的问题
