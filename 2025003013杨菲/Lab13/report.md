# Lab13：创建 Bookshelf 网络书架应用

## 一、为什么改用 Apifox Mock 接口

本实验选择 Apifox Mock 接口而非传统后端服务，主要原因有四点：

第一，无需搭建后端服务器。Apifox Mock 提供开箱即用的测试接口，降低了实验门槛，让学生可以专注于 Android 端开发。

第二，接口支持 HTTPS 协议。这兼容现代 Android 版本的要求，无需额外配置明文流量许可。

第三，响应格式规范标准。接口返回标准的 JSON 数组格式，非常适合练习 Retrofit 加 Gson 的解析组合。

第四，服务稳定可靠。Apifox 作为专业的 API 管理平台，提供的 Mock 服务稳定性有保障。

API 地址为：https://m1.apifoxmock.com/m1/8321477-8085280-default/photos

响应数据包含两个字段：id 字段作为书籍的唯一标识和列表键值，img_src 字段作为图片 URL 传给 Coil 库加载。

---

## 二、Retrofit 服务接口定义

本实验使用 Retrofit 框架定义网络服务接口。接口定义中包含三个关键要素：

第一，使用 GET 注解声明 HTTP 请求类型和路径。注解参数为 photos，会拼接在基础 URL 后面。

第二，使用 suspend 关键字修饰函数。这表示该函数是挂起函数，支持 Kotlin 协程，可以在协程作用域中调用。

第三，函数返回类型为 List。Gson 转换器会自动将 JSON 数组解析为 BookDto 对象列表。

Retrofit 实例通过 Builder 模式创建，需要配置两个参数：基础 URL 地址和 Gson 转换器工厂。

---

## 三、Repository 如何隔离网络数据源

Repository 模式的核心思想是通过接口层将数据获取与数据来源解耦。

在本实验中，BooksRepository 接口定义了数据访问契约，声明了获取书籍列表和获取单本书籍两个方法。

接口有两个实现类：NetworkBooksRepository 通过网络请求 Apifox Mock API 获取真实数据；OfflineBooksRepository 返回本地硬编码的兜底数据，用于断网测试场景。

依赖注入方面，AppContainer 容器负责集中创建 Repository 实例。RealAppContainer 提供真实网络数据源，OfflineAppContainer 提供本地测试数据源。Application 类中初始化容器，可根据需要切换数据源。

Repository 隔离带来的好处包括：ViewModel 不直接依赖 Retrofit 实现了解耦；可以轻松用 Mock 对象替换真实请求提高可测试性；切换数据源只需更换实现类具有灵活性；网络异常时可使用本地兜底数据支持离线场景。

---

## 四、应用的 Loading Success Error 状态切换

本实验定义了三种 UI 状态，使用 Kotlin 密封类实现类型安全的状态管理。

Loading 状态表示数据加载中，UI 层显示圆形进度条和加载中文字。

Success 状态表示加载成功，携带书籍列表数据，UI 层显示图片网格。

Error 状态表示加载失败，携带错误信息，UI 层显示错误提示和重试按钮。

状态管理采用 StateFlow 实现。ViewModel 中创建 MutableStateFlow 状态流，对外暴露只读的 StateFlow。UI 层通过 collectAsState 收集状态，当状态变化时自动重组界面。

状态切换流程如下：应用启动时初始状态为 Loading，ViewModel 发起网络请求。请求成功后切换到 Success 状态并传递书籍列表，UI 显示图片网格。请求失败后切换到 Error 状态并传递错误信息，UI 显示重试按钮。用户点击重试按钮后，重新切换到 Loading 状态，重复上述流程。

交互功能方面，用户点击任意图片时，ViewModel 记录选中的书籍，UI 层弹出 AlertDialog 详情弹窗，显示大图预览和书籍编号。点击关闭按钮可关闭弹窗。

---

## 五、项目配置要点

配置方面需要完成三件事。

依赖添加：在 app 模块的 build.gradle.kts 中添加 Retrofit 网络库、Gson 转换器、Coil 图片加载库和 ViewModel 扩展库。

权限声明：在 AndroidManifest.xml 中添加网络权限 INTERNET。

Application 注册：创建自定义 Application 类，在其中初始化依赖注入容器，并在 AndroidManifest.xml 中注册该类。

---

## 六、遇到的问题

第一个问题是 Material3 API 实验性警告。使用 TopAppBar 组件时出现警告提示，解决方案是在 Composable 函数上方添加 OptIn 注解，标记启用实验性 Material3 API。

第二个问题是图片加载失败。可能原因包括网络权限未添加或图片 URL 无效。解决方案是检查 Manifest 中的网络权限，并在浏览器中测试图片 URL 是否可访问。

第三个问题是依赖同步失败。添加依赖后 Gradle Sync 失败，解决方案是检查网络连接，或执行 Invalidate Caches 操作后重启 Android Studio。

---

## 七、总结

本实验完成了一个完整的 Bookshelf 网络书架应用，实现了以下核心功能：使用 Retrofit 发起网络请求获取 JSON 数据，使用 Gson 解析数据为 Kotlin 对象，使用 Repository 模式隔离数据层并通过依赖注入管理数据源，使用 Coil 加载网络图片，使用 LazyVerticalGrid 构建瀑布流网格界面，使用密封类和 StateFlow 管理加载中、成功、失败三种 UI 状态，实现点击图片显示详情弹窗的交互功能。

通过本实验，掌握了 MVVM 加 Repository 架构在 Android 应用中的完整实现流程，理解了状态驱动 UI 的开发模式，以及依赖注入在解耦方面的应用价值。