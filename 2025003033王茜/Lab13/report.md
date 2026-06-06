# Lab13 网络书架 Bookshelf 实验报告
## 一、实验目的
练习 Retrofit、Gson、Coil、Repository分层、ViewModel状态管理、Compose网格布局，使用Apifox Mock接口完成图片书架项目，掌握MVVM架构与数据源解耦。

## 二、选用Apifox Mock接口原因
1. **省去后端部署**：无需自己编写后端接口，快速提供固定JSON测试数据，专注Android客户端代码编写。
2. **数据稳定统一**：接口数据固定不变，全班使用同一个地址，避免接口改动导致项目运行异常，方便实验调试。
3. **支持HTTPS**：原生https地址，不用配置明文网络许可配置，简化AndroidManifest配置。
4. **方便异常测试**：断开网络即可快速测试离线兜底数据源逻辑。

## 三、Retrofit服务接口定义说明
1. 定义BASE_URL：`https://m1.apifoxmock.com/m1/8321477-8085280-default/`，接口端点`photos`。
2. 使用`@GET("photos")`注解，配合suspend挂起函数，依托协程在子线程执行网络请求，避免主线程阻塞。
3. 返回`Response<List<BookDto>>`，通过`.body()`拿到返回的JSON数组数据。
4. Retrofit单例通过lazy懒加载创建，全局只实例化一次，节省资源。

## 四、Repository分层设计与数据源隔离
1. 定义顶层抽象接口`BooksRepository`，规范`getBooks()、getBook()`方法，ViewModel只依赖接口，不直接耦合Retrofit网络代码。
2. `NetworkBooksRepository`：实现在线数据源，调用Retrofit接口获取网络DTO，并在仓库层完成`BookDto→Book`领域模型转换，隔离网络数据结构。
3. `OfflineBooksRepository`：离线本地假数据源，断网异常时自动兜底，保证应用不会崩溃白屏。
4. AppContainer统一管理所有依赖（Api、Repository），配合Application实现全局依赖注入，方便切换在线/离线数据源。

## 五、UI三种状态切换逻辑（Loading/Success/Error）
项目采用密封接口`BookshelfUiState`管理页面状态：
1. **Loading状态**：调用加载方法时，立刻赋值为Loading，页面展示圆形加载动画。
2. **Success状态**：网络请求正常获取数据后，切换Success并携带图书列表，Compose渲染`LazyVerticalGrid`图片网格。
3. **异常兜底**：网络请求捕获Exception后，不进入Error页面，自动加载离线Repository本地数据，保证页面正常展示；原始Error页面预留，可用于手动异常重试。
4. 点击Item修改selectedBook，唤起AlertDialog详情弹窗，关闭弹窗置空选中对象。

## 六、程序运行效果
1. **联网状态**：成功请求Apifox远程JSON，Coil的AsyncImage加载在线图片，网格展示所有图书封面，单击图片弹出详情大图弹窗。
2. **断网状态**：捕获网络异常，自动读取Offline本地mock数据，页面正常渲染本地图片，无闪退空白。
3. 页面包含顶部标题栏、加载指示器、错误重试按钮、图片网格、详情弹窗五大模块。

## 七、实验问题与解决方案
1. **JSON字段img_src带下划线，与kotlin命名冲突**
    解决：DTO中使用`@SerializedName("img_src")`注解做字段映射。
2. **无网络环境请求崩溃**
    解决：try-catch捕获异常，异常分支自动切换离线仓库数据源。
3. **图片加载错乱**
    解决：使用Coil AsyncImage替代原生Image，自动处理图片缓存与异步加载。

## 八、实验总结
通过本次实验掌握了分层架构：网络层→数据仓库层→ViewModel层→Compose UI层，实现数据源与视图解耦，理解Mock接口在开发调试中的作用，熟练使用Retrofit网络请求、Coil远程图片加载、密封类管理UI多状态。