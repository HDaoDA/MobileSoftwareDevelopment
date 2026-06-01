# Lab13：Bookshelf 网络书架应用 实验报告

## 一、实验目的
本实验通过开发一个网络书架应用，综合练习 Retrofit 网络请求、Gson 数据解析、Coil 图片加载、Repository 模式与 ViewModel 状态管理，加深对 Android Compose 架构与网络请求流程的理解。

## 二、为什么使用 Apifox Mock 接口
- 提供稳定、可控的测试数据，不受后端服务变动影响
- 无需自己搭建后端，专注于客户端代码实现
- 接口格式简单，适合学习 Retrofit 的使用

## 三、Retrofit 服务接口定义
1. 定义 `BookshelfApiService` 接口，使用 `@GET("photos")` 声明请求
2. 使用 `suspend fun getBooks()` 支持协程调用
3. 返回 `List<BookDto>`，通过 Gson 自动解析 JSON 数据

## 四、Repository 如何隔离网络数据源
- `BooksRepository` 作为抽象接口，定义获取书籍数据的方法
- `NetworkBooksRepository` 实现接口，通过 Retrofit 从网络获取数据
- `OfflineBooksRepository` 作为兜底实现，提供本地模拟数据
- `AppContainer` 统一管理 Repository 实例，便于切换数据源，降低 ViewModel 与网络层的耦合

## 五、UI 状态切换逻辑
- **Loading 状态**：页面启动时显示加载进度条
- **Success 状态**：网络请求成功后，通过 `LazyVerticalGrid` 显示书籍图片网格
- **Error 状态**：请求失败时显示错误提示与重试按钮
- 点击图片时，通过弹窗显示书籍详情

## 六、运行截图
见 `screenshot.png` 文件。

## 七、遇到的问题与解决方法
1.  **`collectAsStateWithLifecycle` 报错**
    - 原因：依赖版本不匹配或未同步
    - 解决：改用 `collectAsState` 临时实现，或检查 lifecycle 依赖版本
2.  **`@SerializedName` 报错**
    - 原因：缺少 Gson 依赖或导入错误
    - 解决：添加 `implementation("com.google.code.gson:gson:2.11.0")` 并正确导入 `com.google.gson.annotations.SerializedName`
3.  **`TopAppBar` 实验性警告**
    - 原因：Material3 API 处于实验阶段
    - 解决：添加 `@OptIn(ExperimentalMaterial3Api::class)` 注解

## 八、实验总结
通过本次实验，我掌握了 Retrofit 网络请求、Repository 数据隔离、Compose 状态管理等核心技能，理解了 Android 应用从数据层到 UI 层的完整流程，为后续更复杂的网络应用开发打下了基础。