# Lab13 实验报告

## 实验目的

完成一个可运行的 Bookshelf 应用，练习使用 Retrofit+Gson 请求网络数据、使用 Repository 隔离数据层、用 Coil 加载图片，并在 Compose 中展示 Loading/Success/Error 三类 UI 状态。

## 为什么使用 Apifox Mock

- 提供稳定且可重复的测试数据，避免真实服务不稳定造成结果不可复现。
- 便于调试和批改：数据格式固定、图片 URL 可直接打开确认。

## Retrofit 服务定义（示例）

```kotlin
const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"

interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

其中 `BookDto` 对应 Mock 返回结构：

```kotlin
data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)
```

并在 Retrofit 构建时添加 `GsonConverterFactory`。

## Repository 与依赖注入

- 定义 `BooksRepository` 接口，将网络实现与离线兜底实现解耦：

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

- `NetworkBooksRepository` 通过 `BookshelfApiService` 拉取数据并把 `BookDto` 转换为 `Book`。
- `OfflineBooksRepository` 在网络不可用时返回本地静态数据，保证应用可运行用于演示。
- `AppContainer` 集中创建 `Retrofit`、`BookshelfApiService` 与 `BooksRepository`（便于替换与测试）。

## UI 状态与 `ViewModel` 设计

- 使用 sealed 接口表示界面状态：

```kotlin
sealed interface BookshelfUiState {
    object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}
```

- `BookshelfViewModel` 在初始化时通过 `viewModelScope` 调用 `BooksRepository.getBooks()`：
  - 请求开始：设置 `uiState = Loading`。
  - 请求成功：设置 `uiState = Success(books)`。
  - 请求失败：设置 `uiState = Error(message)`，同时提供重试方法。
  - 点击条目：在 ViewModel 中记录选中项，Compose 根据其显示详情弹窗；关闭弹窗则清除选中项。

## Compose 界面要点

- 顶部标题栏（`TopAppBar`）。
- 加载时显示 `CircularProgressIndicator`。
- 错误时显示错误信息和 `Button` 触发 ViewModel 的重试。
- 使用 `LazyVerticalGrid` 展示图片网格；每项使用 `Card` 包裹并用 Coil 的 `AsyncImage` 加载 `Book.coverUrl`。
- 点击项弹出 `AlertDialog` 显示大图与更多信息。

示例图片加载：

```kotlin
AsyncImage(
    model = book.coverUrl,
    contentDescription = book.title,
    contentScale = ContentScale.Crop
)
```

## 运行与验证步骤

1. 在 `AndroidManifest.xml` 中添加网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

2. 在 Android Studio 打开 `Lab13/basic-android-kotlin-compose-training-bookshelf/` 并运行到模拟器或真机。
3. 验证首页显示远程图片网格；点击任意条目可打开详情弹窗。
4. 断网重启应用，验证是否使用离线兜底数据展示界面。

## 截图

- 请将运行截图保存为 `screenshot.png` 并放入本目录。

## 遇到的问题与解决

- 图片不显示：检查 `img_src` 在浏览器是否能打开，确认 Coil 与网络权限已配置。
- Gson 字段命名不匹配：为 DTO 使用 `@SerializedName("img_src")` 映射。
- 网络异常或超时：为用户显示错误界面并提供重试按钮，或使用 `OfflineBooksRepository` 兜底数据。

## 提交文件清单

- `BookshelfApplication.kt`
- `Book.kt`
- `BookDto.kt`
- `BookshelfApiService.kt`
- `ApiConfig.kt`
- `AppContainer.kt`
- `BooksRepository.kt`
- `BookshelfViewModel.kt`
- `BookshelfScreen.kt`
- `screenshot.png` (运行截图)
- `report.md`（本文件）

---

如需我把示例代码片段扩展为完整的 Kotlin 文件或帮你将 `screenshot.png` 占位图加入目录，我可以继续处理。
