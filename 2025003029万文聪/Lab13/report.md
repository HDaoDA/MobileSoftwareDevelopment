# Lab13 Bookshelf 网络书架应用实验报告

## 一、实验目标

本次实验完成一个 Bookshelf 网络书架应用。应用通过 Apifox Mock 接口获取图片数据，使用 Retrofit 请求网络数据，使用 Gson 解析 JSON，使用 Repository 隔离数据层，使用 ViewModel 管理界面状态，并使用 Coil 在 Compose 界面中加载远程图片。

## 二、为什么改用 Apifox Mock 接口

本实验使用 Apifox Mock 接口作为数据源，主要原因是 Mock 接口的数据结构稳定，不需要自己搭建服务器，也不会因为真实业务接口变化导致实验无法运行。这样可以把练习重点放在 Android 网络请求、协程、Repository、ViewModel 和 Compose UI 状态管理上。

本次接口地址为：

```text
https://m1.apifoxmock.com/m1/8321477-8085280-default/photos
```

接口返回的是图片列表，每条数据包含 `id` 和 `img_src`。其中 `id` 用作列表 key 和详情查找标识，`img_src` 传给 Coil 用来加载远程图片。

## 三、Retrofit 服务接口如何定义

网络层中定义了 `BookshelfApiService` 接口：

```kotlin
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
```

其中 `@GET("photos")` 表示访问基础地址后面的 `photos` 路径，`suspend` 表示该函数需要在协程中调用，返回值 `List<BookDto>` 对应接口返回的 JSON 数组。

基础地址定义在 `ApiConfig.kt` 中：

```kotlin
const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"
```

## 四、Repository 如何隔离网络数据源

本实验创建了 `BooksRepository` 接口：

```kotlin
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
```

ViewModel 只依赖 `BooksRepository`，不直接依赖 Retrofit。这样做的好处是：

1. 网络请求代码集中在数据层中，界面层不会直接处理 Retrofit。
2. 如果后续要换成数据库或其他接口，只需要替换 Repository 实现。
3. 更方便测试，因为可以把真实网络数据源替换成离线数据源。

本实验实现了两个 Repository：

- `NetworkBooksRepository`：通过 Retrofit 请求 Apifox Mock 数据。
- `OfflineBooksRepository`：网络失败时提供兜底数据，保证应用仍能显示书架界面。

`AppContainer` 负责集中创建 Retrofit、API Service 和 Repository，这样 MainActivity 和 Composable 不需要自己创建网络对象。

## 五、Loading / Success / Error 状态如何切换

本实验在 ViewModel 中定义了 `BookshelfUiState`：

```kotlin
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>, val selectedBook: Book? = null) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}
```

应用启动时，ViewModel 调用 `loadBooks()`，先把状态设置为 `Loading`，界面显示加载进度条。

当 Repository 成功返回图片列表时，状态切换为 `Success`，界面显示图片网格。

如果加载失败并且没有可用数据，状态切换为 `Error`，界面显示错误信息和“重试”按钮。用户点击“重试”按钮后会再次调用 `loadBooks()`。

用户点击某一本书时，ViewModel 会把当前选中的 `Book` 保存到 `Success.selectedBook` 中，界面显示详情弹窗。关闭弹窗时再把 `selectedBook` 设置为 `null`。

## 六、运行结果

### 首页图片网格

运行后首页显示 Bookshelf 标题栏和图片网格。每个条目显示远程图片和书籍编号。

请将运行截图保存为：

```text
screenshot.png
```

并放在 `Lab13/` 文件夹中。

### 详情弹窗

点击任意条目后，会弹出详情窗口，显示图片、编号和图片地址。点击“关闭”可以关闭弹窗。

## 七、遇到的问题和解决方法

1. 网络请求必须添加 `INTERNET` 权限，否则无法访问 Apifox Mock 接口。已在 `AndroidManifest.xml` 中添加：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

2. JSON 字段名是 `img_src`，Kotlin 属性名使用 `imgSrc`，所以在 `BookDto` 中使用 `@SerializedName("img_src")` 完成字段映射。

3. ViewModel 中不能直接在 Composable 里请求网络，所以通过 `viewModelScope.launch` 启动协程，再调用 Repository 获取数据。

4. 网络异常时，`NetworkBooksRepository` 会使用 `OfflineBooksRepository` 提供的兜底数据，避免应用直接空白或崩溃。

## 八、总结

本次实验综合练习了 Retrofit、Gson、Coil、Repository、ViewModel、协程和 Compose UI。通过 Repository 隔离数据源后，界面层只需要关注状态显示，不需要关心网络请求细节，代码结构更加清晰。
