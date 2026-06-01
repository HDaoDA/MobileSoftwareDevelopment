
# Lab13 网络书架应用实验报告
---

## 一、实验目的
1. 掌握 Retrofit + Gson 实现网络请求与 JSON 数据解析。
2. 理解 Repository 模式，实现网络与本地数据源的隔离与切换。
3. 学习使用 Jetpack Compose 构建网格布局界面，并使用 Coil 加载网络图片。
4. 实现基于 ViewModel 的 UI 状态管理（Loading/Success/Error）。
5. 掌握 Android 应用调试与崩溃日志分析方法。

---

## 二、实验环境
- **开发工具**：Android Studio Hedgehog 或更高版本
- **开发语言**：Kotlin
- **核心框架**：Jetpack Compose、ViewModel、协程（Coroutines）
- **第三方依赖**：Retrofit、Gson、Coil
- **测试设备**：Android 模拟器/真机（API 级别 ≥ 31）
- **接口来源**：Apifox Mock 接口

---

## 三、实验内容与实现

### 1. 项目整体结构
采用分层架构设计，各模块职责清晰：
### 2. 核心模块实现

#### （1）依赖注入与应用初始化
- 通过 `BookshelfApplication` 类初始化 `AppContainer`，统一管理 Retrofit、ApiService、Repository 等核心组件。
- 确保组件单例，便于后续测试与数据源切换。

#### （2）网络层实现
- 使用 Retrofit 定义 `BookshelfApiService` 接口，声明 `GET` 请求获取书籍列表。
- 配置 `BASE_URL`，通过 Gson 解析 JSON 响应，将接口数据映射为 `BookDto` 对象。

#### （3）数据层（Repository）
- 定义 `BooksRepository` 接口，提供 `getBooks()` 方法。
- 实现 `NetworkBooksRepository`：调用网络接口，将 `BookDto` 转换为领域模型 `Book`。
- 实现 `OfflineBooksRepository`：提供本地模拟数据，作为网络异常时的兜底方案。

#### （4）UI 状态管理
- 在 `BookshelfViewModel` 中定义密封类 `BookshelfUiState`，区分 `Loading`、`Success`、`Error` 三种状态。
- ViewModel 持有 Repository 实例，在 `viewModelScope` 中发起协程加载数据，并更新 UI 状态。

#### （5）界面层（Compose）
- `BookshelfScreen` 根据 ViewModel 的 `uiState` 渲染不同界面：
  - `Loading`：显示加载进度条。
  - `Success`：使用 `LazyVerticalGrid` 展示书籍封面网格，点击卡片弹出详情弹窗。
  - `Error`：显示错误信息与重试按钮。
- 使用 Coil 的 `AsyncImage` 加载网络图片，实现书籍封面的异步展示。

---

## 四、运行结果
1. 应用启动后，成功加载并显示书籍封面网格列表。
2. 点击任意书籍封面，弹出详情对话框并展示大图。
3. 网络正常时，数据从 Apifox Mock 接口加载；断网时，自动切换到本地模拟数据，应用不崩溃。
4. 加载失败时，显示错误提示，点击“重试”可重新发起请求。

---

## 五、遇到的问题与解决方法

### 1. 应用启动即崩溃
- **问题原因**：未在 `AndroidManifest.xml` 中注册自定义的 `BookshelfApplication` 类。
- **解决方法**：在 `<application>` 标签中添加 `android:name=".BookshelfApplication"` 属性。

### 2. 无法执行 `adb` 命令查看日志
- **问题原因**：系统环境变量未配置 `adb` 工具路径。
- **解决方法**：使用 Android Studio 内置的 `Terminal` 或直接在 `Logcat` 面板中过滤 `FATAL` 日志。

### 3. `MainActivity` 编译报错
- **问题原因**：代码中调用了不存在的 `BookshelfApp()` 函数。
- **解决方法**：修正导入与调用，改为正确的 `BookshelfScreen()`。

### 4. 网络图片无法加载
- **问题原因**：未添加 `INTERNET` 权限或接口地址配置错误。
- **解决方法**：在 `AndroidManifest.xml` 中添加 `<uses-permission android:name="android.permission.INTERNET" />`，并核对 `BASE_URL`。

---

## 六、实验总结
本次实验成功完成了 Bookshelf 网络书架应用的开发，完整实现了从网络请求、数据解析、业务逻辑处理到界面展示的全流程。通过本次实验，我深入理解了现代 Android 开发中分层架构、依赖注入、状态管理和网络请求的核心思想，掌握了 Jetpack Compose、Retrofit、ViewModel 等关键技术的应用，为后续更复杂的 Android 应用开发打下了坚实的基础。

---
