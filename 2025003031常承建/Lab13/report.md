# Lab13 网络书架应用实验报告
## 学号：2025003031 姓名：常承建

### 一、为什么本实验改用 Apifox Mock 接口
1. 提供稳定可控的模拟数据源，避免依赖真实后端服务，确保实验可复现
2. 支持自定义响应结构和数据量，完美适配本实验的图片列表需求
3. 原生支持HTTPS协议，符合Android 9+的网络安全要求
4. 便于测试加载中、成功、失败等不同状态下的应用表现

### 二、Retrofit 服务接口如何定义
1. 创建`BookshelfApiService`接口，使用`@GET("photos")`注解指定请求路径
2. 定义`suspend fun getBooks(): List<BookDto>`方法，通过suspend关键字支持协程
3. 在`AppContainer`中通过Retrofit.Builder构建实例，指定BASE_URL和Gson转换器
4. 调用`retrofit.create()`生成接口的动态代理实现类

### 三、Repository 如何隔离网络数据源
1. 定义`BooksRepository`接口，声明数据获取的统一规范
2. 实现`NetworkBooksRepository`负责网络数据获取和DTO到领域模型的转换
3. 实现`OfflineBooksRepository`提供硬编码的离线兜底数据
4. ViewModel仅依赖Repository接口，不直接接触Retrofit，实现了数据层与UI层的解耦
5. 可以无缝切换不同数据源，便于单元测试和离线场景支持

### 四、应用的 Loading / Success / Error 状态如何切换
1. 定义密封类`BookshelfUiState`，封装三种核心UI状态
2. ViewModel使用`StateFlow`持有状态，确保生命周期安全
3. 数据加载开始时设置为Loading状态
4. 网络请求成功时设置为Success状态并传递书籍列表
5. 网络请求失败时自动降级到离线数据，若仍失败则设置为Error状态
6. Compose界面通过`collectAsStateWithLifecycle()`收集状态并渲染对应UI

### 五、运行截图
（此处插入screenshot.png）

### 六、遇到的问题及解决方法
1. 问题：图片加载失败
   解决：确认添加了INTERNET权限，检查图片URL在浏览器中可访问
2. 问题：ViewModel创建失败
   解决：实现自定义ViewModelFactory，传递Repository依赖
3. 问题：网络请求在主线程执行崩溃
   解决：使用suspend函数和viewModelScope在后台线程执行请求