# Lab13 实验报告: Bookshelf 网络书架应用

## 一、实验目的

本次实验的主要目的是完成一个基于网络数据的 Bookshelf 网络书架应用。通过本实验，掌握在 Android Compose 项目中使用 Retrofit 请求网络接口，使用 Gson 解析 JSON 数据，使用 Repository 隔离数据层，使用 ViewModel 管理界面状态，并使用 Coil 加载远程图片的方法。

通过本实验，我进一步理解了网络层、数据层、状态管理层和 UI 层之间的分工，能够将网络请求结果展示到 Compose 界面中，并能够处理加载中、加载成功和加载失败等不同状态。


## 二、实验环境

- 开发工具：Android Studio
- 开发语言：Kotlin
- UI 框架：Jetpack Compose
- 网络请求库：Retrofit
- JSON 解析库：Gson
- 图片加载库：Coil
- 架构组件：ViewModel、Repository
- 数据来源：Apifox Mock 接口


## 三、为什么本实验改用 Apifox Mock 接口

本实验使用 Apifox Mock 接口作为数据源，主要原因是 Mock 接口能够提供稳定、可控的测试数据。相比真实业务接口，Mock 接口更适合实验教学场景，因为它不依赖复杂的后端业务逻辑，也不需要自己搭建服务器。

通过 Apifox Mock 接口，应用可以直接请求固定格式的 JSON 数据。这样可以将实验重点集中在 Retrofit 网络请求、Gson 数据解析、Repository 数据封装、ViewModel 状态管理以及 Compose 界面展示上，减少后端环境不稳定带来的影响。

本实验使用的接口返回的是图片列表数据，每条数据包含 `id` 和 `img_src` 字段。其中，`id` 用于列表 key 和详情查找，`img_src` 用于传递给 Coil 加载远程图片


## 四、实验原理

本实验采用分层结构完成 Bookshelf 应用，主要分为网络层、数据层、ViewModel 层和 UI 层。

### 1. 网络层

网络层使用 Retrofit 创建服务接口。Retrofit 通过注解描述 HTTP 请求方式和接口路径。本实验中，`BookshelfApiService` 使用 `@GET("photos")` 请求图片数据列表。

Retrofit 的基础地址为：

```text
https://m1.apifoxmock.com/m1/8321477-8085280-default/
```

接口路径为：

```text
photos
```

二者组合后，应用最终请求的地址就是 Apifox Mock 提供的图片数据接口。

### 2. Gson 数据解析

接口返回的数据是 JSON 数组，每个元素包含 `id` 和 `img_src` 字段。由于 Kotlin 中通常使用驼峰命名，所以在 `BookDto` 中使用 `@SerializedName("img_src")` 将 JSON 字段 `img_src` 映射为 Kotlin 属性 `imgSrc`。

`BookDto` 表示网络层接收到的数据对象，`Book` 表示应用内部使用的数据模型。通过 `asExternalModel()` 方法，可以把 DTO 转换为更适合 UI 使用的领域模型。

### 3. Repository 数据隔离

Repository 的作用是隔离数据来源，让 ViewModel 不直接依赖 Retrofit。这样可以降低代码耦合度，也方便以后替换数据源。

本实验中定义了 `BooksRepository` 接口，并提供了两个实现：

- `NetworkBooksRepository`：通过 Retrofit 请求 Apifox Mock 接口获取数据；
- `OfflineBooksRepository`：提供离线兜底数据，在网络异常时仍然可以显示界面。

这样即使网络请求失败，应用也可以使用离线数据展示书架界面，提高了程序的稳定性。

### 4. 依赖注入

本实验通过 `AppContainer` 集中创建 Retrofit、API service 和 Repository。`BookshelfApplication` 在应用启动时创建 `AppContainer`，ViewModel 再从 Application 中获取 Repository。

这种方式可以避免在多个地方重复创建 Retrofit，也让项目结构更加清晰。

### 5. ViewModel 与 UI 状态

ViewModel 负责加载数据并维护 UI 状态。本实验设计了三种状态：

- `Loading`：正在加载数据；
- `Success`：数据加载成功，显示图片网格；
- `Error`：数据加载失败，显示错误信息和重试按钮。

应用启动后，ViewModel 会自动调用 `getBooks()` 加载数据。加载过程中先将状态设置为 `Loading`，请求成功后设置为 `Success`，如果发生异常则设置为 `Error`。

同时，ViewModel 还负责处理点击条目和关闭详情弹窗。当用户点击某一本书时，ViewModel 会记录当前选中的 `Book`，界面根据该数据弹出详情对话框。

### 6. Compose UI 界面

界面层使用 Jetpack Compose 实现，主要包括：

- 顶部标题栏；
- 加载中进度指示器；
- 错误提示和重试按钮；
- `LazyVerticalGrid` 图片网格；
- 每个书籍条目的 Card；
- 点击条目后显示详情弹窗。

图片加载使用 Coil 的 `AsyncImage` 组件，将 `Book` 中的 `coverUrl` 传入 `model` 参数，从而加载远程图片。


## 五、实验步骤

### 1. 创建项目

首先在 Android Studio 中创建 Empty Activity 项目，项目包名设置为：

```text
com.example.bookshelf
```

### 2. 添加依赖

在 `app/build.gradle.kts` 中添加 Retrofit、Gson Converter、Coil 和 ViewModel 相关依赖。

主要依赖包括：

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("io.coil-kt:coil-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
```

### 3. 添加网络权限

在 `AndroidManifest.xml` 中添加网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

如果不添加该权限，应用无法正常访问网络接口，也无法加载远程图片。

### 4. 创建数据模型

创建 `BookDto` 对应网络返回的数据结构，创建 `Book` 作为应用内部使用的数据结构，并通过 `asExternalModel()` 完成数据转换。

### 5. 创建 Retrofit 服务接口

创建 `BookshelfApiService`，使用 `@GET("photos")` 定义请求接口，通过 `suspend fun getBooks()` 返回图书图片列表。

### 6. 创建 Repository

创建 `BooksRepository` 接口，并分别实现网络数据仓库和离线数据仓库。网络仓库负责从 Apifox Mock 接口获取数据，离线仓库负责在网络异常时提供兜底数据。

### 7. 创建 AppContainer

在 `AppContainer` 中创建 Retrofit 实例、API service 实例和 Repository 实例，实现简单的依赖注入。

### 8. 创建 ViewModel

在 `BookshelfViewModel` 中调用 Repository 获取数据，并根据请求结果更新 UI 状态。ViewModel 中还实现了重试、选择图书和关闭详情弹窗等逻辑。

### 9. 创建 Compose 界面

在 `BookshelfScreen` 中根据不同 UI 状态显示不同界面：

- Loading 状态显示进度指示器；
- Success 状态显示图片网格；
- Error 状态显示错误提示和重试按钮。

图片网格使用 `LazyVerticalGrid` 实现，单个条目使用 `Card` 和 `AsyncImage` 实现。点击条目后，通过 `AlertDialog` 显示详情信息。


## 六、运行结果

程序运行后，首页显示 Bookshelf 网络书架界面。应用会自动从 Apifox Mock 接口请求图片数据，并以网格形式展示图片。

每个条目显示一张图片和对应编号。点击任意图片后，会弹出详情窗口，显示该图书的编号和图片地址。点击“关闭”按钮后，详情弹窗消失。

当网络异常时，Repository 会使用离线兜底数据，保证应用仍然可以显示基本界面。

运行截图文件为：

```text
screenshot.png
```


## 七、遇到的问题与解决方法

### 1. 忘记添加网络权限

如果没有在 `AndroidManifest.xml` 中添加 `INTERNET` 权限，应用无法访问 Apifox Mock 接口，也无法加载远程图片。解决方法是在 Manifest 中添加：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 2. JSON 字段名和 Kotlin 属性名不一致

接口返回字段名是 `img_src`，而 Kotlin 中更常用 `imgSrc`。如果不进行映射，Gson 可能无法正确解析图片地址。解决方法是在 DTO 中使用：

```kotlin
@SerializedName("img_src")
val imgSrc: String = ""
```

### 3. ViewModel 不应直接请求 Retrofit

如果在 Composable 中直接请求 Retrofit，会导致界面和数据层耦合严重，也不方便处理状态。本实验通过 Repository 和 ViewModel 进行分层，让 Composable 只负责显示界面。

### 4. 图片加载失败

如果图片加载失败，可以先复制 `img_src` 地址到浏览器中测试是否能打开。如果浏览器也打不开，说明图片地址或网络环境存在问题。如果浏览器能打开，则需要检查 Coil 依赖、网络权限和 `AsyncImage` 写法是否正确。

## 八、实验总结

通过本次实验，完成了一个基于网络数据的 Bookshelf 网络书架应用。实验过程中，我学习并实践了 Retrofit 网络请求、Gson JSON 解析、Repository 数据隔离、ViewModel 状态管理以及 Coil 图片加载等内容。
本实验让我更加清楚地理解了 Android 应用中常见的分层架构。网络层负责请求数据，数据层负责统一管理数据来源，ViewModel 负责维护界面状态，Compose UI 负责根据状态展示不同界面。这样的结构使代码更加清晰，也更容易维护和扩展。
同时，本实验还让我理解了 Loading、Success 和 Error 状态的重要性。一个完整的网络应用不能只考虑请求成功的情况，还需要处理加载中和失败时的界面反馈。通过添加重试按钮和离线兜底数据，应用的可用性得到了提升。