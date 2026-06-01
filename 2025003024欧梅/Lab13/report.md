# Lab13：创建 Bookshelf 网络书架应用 实验报告

## 一、实验基本信息
- 实验名称：创建 Bookshelf 网络书架应用
- 实验时间：2026年
- 实验环境：Android Studio、Kotlin、Jetpack Compose
- 实验设备：Android 模拟器

## 二、实验目的
1. 掌握 Retrofit 网络请求接口的定义与使用方法。
2. 掌握 Gson 数据解析，区分 DTO 与领域模型。
3. 掌握 Repository 数据层模式，实现网络数据源与离线数据源隔离。
4. 掌握 AppContainer 实现简单依赖注入，便于切换数据源。
5. 掌握协程 + ViewModel + StateFlow 实现 UI 状态管理。
6. 实现 Loading / Success / Error 三种状态界面切换。
7. 使用 Coil 完成网络图片加载，使用 LazyVerticalGrid 实现网格布局。
8. 实现书籍封面点击弹窗详情功能，完成完整交互流程。

## 三、实验内容与实现步骤
1. 创建项目，添加 Retrofit、Gson、Coil、ViewModel 依赖。
2. 在 AndroidManifest.xml 中添加 INTERNET 网络权限。
3. 编写数据模型：BookDto（网络解析）、Book（UI 使用）。
4. 实现网络层：Retrofit 实例、BookshelfApiService 接口。
5. 实现数据层：BooksRepository 接口、Network 与 Offline 两种数据源。
6. 通过 AppContainer + Application 实现全局依赖注入。
7. 定义密封接口 BookshelfUiState，实现三种 UI 状态。
8. 编写 ViewModel，处理数据加载、状态切换、重试、书籍选择逻辑。
9. 使用 Compose 构建界面：网格列表、卡片、加载动画、错误提示、详情弹窗。
10. 测试网络模式与离线模式，确保界面正常显示、图片加载、点击弹窗功能可用。

## 四、核心问题回答（实验要求必须写）
### 1. 为什么本实验改用 Apifox Mock 接口
- Apifox Mock 提供稳定、固定格式的 JSON 数据，不受后端服务影响。
- 接口返回结构可控，便于调试网络请求与数据解析。
- 无需搭建服务器，适合课堂实验练习网络编程。
- HTTPS 接口安全可靠，无需配置明文流量。

### 2. Retrofit 服务接口如何定义
- 定义接口使用 @GET("photos") 标注请求方法与路径。
- 方法使用 suspend 关键字，支持协程。
- 返回值为 List<BookDto>，由 Gson 自动解析。
- 通过 Retrofit Builder 设置 BASE_URL 与 Gson 转换器，创建 ApiService 实例。

### 3. Repository 如何隔离网络数据源
- Repository 采用接口 + 实现类结构，ViewModel 只依赖接口。
- NetworkBooksRepository 从网络获取数据。
- OfflineBooksRepository 提供本地兜底数据。
- 通过 AppContainer 切换数据源，无需修改 ViewModel 与 UI 代码。
- 实现数据层与 UI 层解耦，便于测试、维护与扩展。

### 4. 应用的 Loading / Success / Error 状态如何切换
- 进入页面 → 状态设置为 Loading → 显示进度圈。
- 网络请求成功 → 状态改为 Success → 显示图片网格。
- 网络异常/失败 → 状态改为 Error → 显示错误文字与重试按钮。
- 点击重试 → 重新进入 Loading → 再次请求数据。
- 所有状态由 ViewModel 通过 StateFlow 管理，Compose 自动观察并刷新界面。

## 五、运行结果
1. 应用启动正常，无崩溃。
2. 能够从 Apifox Mock 接口加载书籍封面图片。
3. 使用 LazyVerticalGrid 以两列网格展示书籍封面。
4. 加载中显示转圈动画，加载失败显示错误与重试。
5. 点击任意封面弹出详情大图弹窗。
6. 切换离线数据源可显示兜底图片，支持断网运行。
7. 界面布局美观，交互正常，图片加载流畅。

## 六、实验遇到的问题及解决方法
1. 问题：应用启动崩溃
   解决：未在 AndroidManifest.xml 注册 BookshelfApplication，添加 android:name 后修复。
2. 问题：Repository 方法报错类型不匹配
   解决：接口方法与实现类都必须加 suspend，保持一致。
3. 问题：图片无法加载
   解决：添加 INTERNET 权限，检查接口地址是否正确。
4. 问题：UI 状态不更新
   解决：使用 StateFlow 并正确 collectAsState，确保协程正常执行。

## 七、实验总结
本次实验完成了一个完整的网络图片书架应用，熟练掌握了 Android 网络开发、MVVM 架构、状态管理、图片加载、界面布局等核心技能。通过 Repository 与依赖注入实现了清晰的分层架构，代码结构规范、易于扩展。实验达到了所有预期目标，能够稳定运行并满足所有实验要求。