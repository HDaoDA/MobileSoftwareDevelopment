# Lab13：创建 Bookshelf 网络书架应用 实验报告
## 学号姓名：2025003022 柳航

## 一、实验选用Apifox Mock接口的原因
1. Apifox Mock 接口数据固定、服务长期稳定在线，不受后端启停、接口改动影响，保证实验环境稳定。
2. 无需自己搭建后端服务，省去服务部署、数据库配置工作，聚焦 Retrofit、Repository、Coil 等知识点练习。
3. 可稳定模拟真实线上接口返回格式，方便测试正常加载、断网异常、解析报错等各类边界场景。

## 二、Retrofit 服务接口定义说明
1. 在`ApiConfig.kt`定义常量`BASE_URL`，存放接口根路径，作为 Retrofit 构建参数。
2. 新建`BookshelfApiService`接口，通过`@GET("photos")`注解声明GET请求接口。
3. 请求方法使用`suspend`修饰，依托Kotlin协程实现异步请求，返回`List<BookDto>`。
4. Retrofit构建时引入Gson转换器，自动完成JSON字符串与`BookDto`实体类的序列化与反序列化。

## 三、Repository如何隔离网络数据源
1. 抽象层：定义`BooksRepository`顶层接口，规范`getBooks()`、`getBook()`两个数据查询方法。
2. 网络实现：`NetworkBooksRepository`依赖ApiService，通过Retrofit从Apifox接口拉取远程数据，并将`BookDto`转为领域模型`Book`。
3. 离线兜底实现：`OfflineBooksRepository`内置静态本地数据，断网时替换网络数据源。
4. 解耦优势：ViewModel只依赖抽象`BooksRepository`，不直接耦合Retrofit，一键切换在线/离线数据源，便于单元测试。

## 四、应用Loading/Success/Error三种UI状态切换逻辑
通过密封类`BookshelfUiState`拆分页面状态，由ViewModel的StateFlow驱动页面刷新：
1. **Loading（加载中）**
页面初始化、点击重试按钮时触发，界面全屏展示圆形加载进度指示器。
2. **Success（加载成功）**
网络请求正常拿到数据后切换状态，使用`LazyVerticalGrid`三列网格布局，Coil的`AsyncImage`加载远程封面图；点击卡片弹出AlertDialog详情弹窗。
3. **Error（加载失败）**
网络中断、接口异常时进入该状态，页面展示错误提示文字+重试按钮，点击重试重新调用加载逻辑，页面切回Loading。

## 五、运行截图说明
`screenshot.png`为本项目真机运行效果图：
1. 首页网格成功加载多张远程在线图书封面图片，图片异步加载无卡顿；
2. 点击任意图片卡片，正常弹出详情弹窗，展示原图大图，可关闭弹窗；
3. 断开设备网络重启App，程序自动读取离线Repository内置兜底数据，页面正常渲染。

## 六、实验过程遇到的问题与解决方案
1. **图片空白无法加载**
原因：图片URL失效/缺少网络权限；解决：浏览器校验图片链接，AndroidManifest添加INTERNET权限，核对Coil依赖版本。
2. **JSON解析报错**
原因：JSON字段`img_src`下划线与实体属性命名不一致；解决：在BookDto属性添加`@SerializedName("img_src")`字段映射注解。
3. **主线程网络崩溃**
原因：未使用协程在主线程发起请求；解决：接口方法添加suspend，ViewModel借助viewModelScope发起网络调用。