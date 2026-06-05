# Lab13 网络书架应用实验报告
---

## 一、实验目的
1. 熟练运用 Retrofit 结合 Gson 完成网络数据请求及 JSON 格式数据的解析处理。
2. 深度理解 Repository 设计模式的核心思想，实现网络数据源与本地兜底数据源的解耦及灵活切换。
3. 掌握 Jetpack Compose 中网格布局（Grid）的构建方式，以及 Coil 库在异步加载网络图片场景下的应用。
4. 基于 ViewModel 与协程实现 UI 状态的精细化管理，涵盖加载中（Loading）、加载成功（Success）、加载失败（Error）等状态。
5. 掌握 Android 应用调试的常用手段，能够分析崩溃日志定位并解决常见问题。

---

## 二、实验环境
- **开发工具**：Android Studio Iguana 2023.2.1 及以上版本
- **开发语言**：Kotlin（JDK 17）
- **核心框架**：Jetpack Compose（UI）、ViewModel（状态管理）、Coroutines（协程）
- **第三方依赖**：Retrofit 2.9.0（网络请求）、Gson Converter（JSON 解析）、Coil 2.4.0（图片加载）
- **测试设备**：Android 模拟器（Pixel 6，API 33）/ 真机（Android 13）
- **接口来源**：Apifox 提供的 Mock 测试接口

---

## 三、实验内容与实现

### 1. 项目整体架构设计
采用“应用层-数据层-网络层-UI层”的分层架构，各层职责边界清晰：
- 应用层（Application）：初始化全局组件容器，统一管理核心依赖；
- 网络层：封装 Retrofit 配置与 API 接口定义，处理网络请求；
- 数据层（Repository）：抽象数据源接口，实现网络/本地双数据源；
- 视图模型层（ViewModel）：处理业务逻辑，管理 UI 状态；
- UI 层（Compose）：根据状态渲染界面，响应用户交互。

### 2. 核心模块实现细节

#### （1）全局组件管理（AppContainer & Application）
- 自定义 `BookshelfApplication` 继承 `Application`，在 `onCreate` 中初始化 `AppContainer`；
- `AppContainer` 通过懒加载（lazy）创建 `NetworkBooksRepository` 实例，确保组件单例且按需初始化，降低内存占用。

#### （2）网络请求层实现
- 定义 `BookshelfApiService` 接口，通过 `@GET` 注解声明获取书籍列表的接口方法，指定请求路径为 `photos`；
- `ApiConfig` 类封装 Retrofit 初始化逻辑，配置 `BASE_URL` 并添加 Gson 转换器，将 JSON 数据自动映射为 `BookDto` 实体类；
- `BookDto` 作为接口数据载体，通过 `@SerializedName` 处理 JSON 字段与 Kotlin 字段的命名差异（如 `img_src` 映射为 `imgSrc`）。

#### （3）数据层（Repository 模式）
- 抽象 `BookshelfRepository` 接口，定义 `getBooks()`（获取书籍列表）和 `getBook(id: String)`（获取单本书籍）两个核心方法；
- `NetworkBooksRepository` 实现接口：调用 Retrofit 接口获取 `BookDto` 列表，通过扩展函数 `asExternalModel()` 转换为领域模型 `Book`，异常时返回空列表；
- `OfflineBooksRepository` 作为兜底实现：预定义本地模拟书籍数据，断网时提供数据，避免应用无数据展示。

#### （4）UI 状态管理（ViewModel + 协程）
- 定义密封类 `BookshelfUiState`，封装 Loading（加载中）、Success（加载成功，携带书籍列表）、Error（加载失败，携带错误信息）三种状态；
- `BookshelfViewModel` 持有 `BooksRepository` 实例，在 `viewModelScope` 协程中调用 `getBooks()` 加载数据，根据结果更新 `_uiState`（MutableStateFlow）；
- 暴露不可变的 `uiState`（StateFlow）供 UI 层收集，同时通过 `_selectedBook` 管理选中书籍状态，实现详情弹窗的展示与关闭。

#### （5）Compose 界面实现
- `BookshelfScreen` 作为主界面：
  - 通过 `LocalContext` 获取 `AppContainer` 中的 Repository 实例，注入 ViewModel；
  - 收集 `uiState` 与 `selectedBook` 状态，根据状态渲染不同界面；
- 状态渲染逻辑：
  - Loading 状态：居中显示 `CircularProgressIndicator` 加载指示器；
  - Success 状态：通过 `LazyVerticalGrid` 实现 2 列网格布局，展示 `BookCard` 列表；
  - Error 状态：显示错误信息与重试按钮，点击重试触发 `viewModel.retry()` 重新加载数据；
- 交互实现：
  - 书籍卡片点击：调用 `selectBook()` 更新选中状态，弹出 `AlertDialog` 展示书籍详情；
  - 详情弹窗：使用 `AsyncImage` 加载书籍封面图片，支持关闭弹窗操作；
  - `BookCard` 组件：封装卡片样式，包含封面图片与书籍标题，限制标题单行显示。

---

## 四、运行结果
1. 应用启动后，优先显示加载指示器，成功加载网络数据后展示书籍网格列表；
2. 点击书籍卡片，弹出详情对话框，可查看书籍 ID、标题及大图封面，点击“Close”关闭弹窗；
3. 网络异常（断网/接口不可用）时，`NetworkBooksRepository` 返回空列表，ViewModel 触发 Error 状态，界面显示错误提示；
4. 切换为 `OfflineBooksRepository` 后，断网状态下可正常展示本地模拟书籍数据，应用无崩溃；
5. 错误状态下点击“Retry”按钮，可重新发起网络请求，成功后更新为 Success 状态并展示数据。

---

## 五、遇到的问题与解决方法

### 1. 数据转换后书籍标题为空
- **问题原因**：`BookDto.asExternalModel()` 中标题固定为 `Book $id`，但接口返回的 `id` 为空字符串，导致标题显示异常；
- **解决方法**：优化转换逻辑，为 `id` 为空时设置默认标题（如 `Unknown Book`），同时核对接口返回的 `id` 字段有效性。

### 2. Retrofit 请求报 `ConnectException`
- **问题原因**：`BASE_URL` 配置错误（末尾缺少 `/`），导致 Retrofit 拼接请求路径时出错；
- **解决方法**：修正 `BASE_URL` 为完整路径（以 `/` 结尾），确保 Retrofit 正确拼接 `photos` 路径。

### 3. Compose 网格布局间距异常
- **问题原因**：`LazyVerticalGrid` 未设置 `contentPadding`，导致边缘卡片与屏幕边界无间距；
- **解决方法**：添加 `contentPadding = PaddingValues(8.dp)`，并通过 `horizontalArrangement`/`verticalArrangement` 设置卡片间距。

### 4. ViewModel 注入失败，报 ClassCastException
- **问题原因**：ViewModel 工厂类未正确转换泛型类型，导致创建实例时类型不匹配；
- **解决方法**：在工厂类 `create` 方法中添加 `@Suppress("UNCHECKED_CAST")`，确保 `BookshelfViewModel` 正确转换为泛型类型 `T`。

---

## 六、实验总结
本次实验基于 Jetpack Compose 与 Retrofit 完成了网络书架应用的开发，完整覆盖了“网络请求-数据解析-状态管理-界面展示”全流程。通过本次实验，深入理解了分层架构的设计思想，掌握了 Repository 模式在多数据源切换场景下的应用，以及 ViewModel + StateFlow 实现 UI 状态驱动的核心逻辑。

同时，在调试过程中解决了网络请求、数据转换、Compose 布局等方面的问题，提升了 Android 应用开发与调试能力。后续可进一步优化方向：① 实现网络状态监听，自动切换网络/本地数据源；② 增加书籍搜索功能；③ 优化图片加载体验（添加占位图、错误图）；④ 接入 Room 数据库实现本地数据持久化。