# 实验报告：Sports 应用的自适应布局实现

## 一、实验背景

随着 Android 设备形态的多样化（手机、折叠屏、平板），应用需要能够适配不同屏幕尺寸，提供最佳的交互体验。Jetpack Compose 提供了 `material3-window-size-class` 库，通过 `WindowSizeClass` API 可以获取当前设备的窗口尺寸类别，从而在布局层面做出响应式调整。

本次实验针对一个名为 **Sports** 的运动资讯浏览应用，该应用起始代码在手机设备上可以正常使用（单窗格导航），但未对平板等大屏设备进行优化。实验目标是为其添加大屏自适应布局，在展开宽度设备上实现“列表-详情”并排显示的规范布局。

## 二、实验目标

- 掌握 `calculateWindowSizeClass()` 的使用方法，获取窗口尺寸类别。
- 理解 `WindowWidthSizeClass.Compact` 与 `Expanded` 的区分。
- 实现单窗格/双窗格布局的自适应切换。
- 根据布局类型调整 `TopAppBar` 的标题与导航图标。
- 正确处理大屏并排模式下的系统返回键行为。

## 三、实验环境与工具

- 开发语言：Kotlin
- UI 框架：Jetpack Compose + Material 3
- 核心依赖：`androidx.compose.material3:material3-window-size-class`
- 开发工具：Android Studio Hedgehog 或更高版本
- 测试设备：手机模拟器（Compact）与平板模拟器（Expanded）

## 四、实验内容与实现过程

### 4.1 原始项目分析

原始 Sports 应用包含 11 种运动的本地数据，UI 分为列表页和详情页，通过 ViewModel 中的 `isShowingListPage` 状态进行单窗格切换。所有设备均使用同一套布局，无法利用大屏空间。

项目已预先定义了 `SportsContentType` 枚举（`ListOnly` 与 `ListAndDetail`），并在 `build.gradle.kts` 中引入了窗口尺寸类别依赖，但未被使用。

### 4.2 任务一：计算并传递 WindowSizeClass

在 `MainActivity.kt` 的 `setContent` 中调用 `calculateWindowSizeClass(activity = this)`，提取 `widthSizeClass` 并传递给 `SportsApp` 可组合项。

**关键代码：**

kotlin

```
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val windowSizeClass = calculateWindowSizeClass(activity = this)
SportsApp(windowWidthSizeClass = windowSizeClass.widthSizeClass)
```



### 4.3 任务二：SportsApp 根据窗口宽度决策布局类型

修改 `SportsApp` 函数签名，接收 `WindowWidthSizeClass` 参数。根据该参数判断：

- 若为 `Expanded` → `SportsContentType.ListAndDetail`
- 否则 → `SportsContentType.ListOnly`

在 Scaffold 内部根据 `contentType` 分支渲染对应的界面。

**核心逻辑：**

kotlin

```
val contentType = when (windowWidthSizeClass) {
    WindowWidthSizeClass.Expanded -> SportsContentType.ListAndDetail
    else -> SportsContentType.ListOnly
}
```



### 4.4 任务三：创建双窗格并排布局 `SportsListAndDetails`

新增私有 Composable `SportsListAndDetails`，使用 `Row` 将 `SportsList`（左侧）与 `SportsDetail`（右侧）并排放置。利用 `Modifier.weight(1f)` 和 `weight(2f)` 分配 1:2 的宽度比例。点击左侧列表项时仅更新右侧详情内容，无需页面跳转。

**关键结构：**

kotlin

```
Row(modifier = modifier.padding(contentPadding)) {
    SportsList(..., modifier = Modifier.weight(1f))
    SportsDetail(..., modifier = Modifier.weight(2f), enableBackHandler = false)
}
```



### 4.5 任务四：调整 SportsAppBar 适配大屏

为 `SportsAppBar` 增加 `isListAndDetail` 参数。当处于大屏并排模式时：

- 标题固定为 “Sports”（`R.string.list_fragment_label`）
- 不显示返回导航按钮

小屏模式保持原有行为（列表页标题为 “Sports”，详情页标题为 “Sport Info” 且显示返回按钮）。

**实现：**

kotlin

```
navigationIcon = {
    if (!isListAndDetail && !isShowingListPage) {
        IconButton(onClick = ...) { Icon(...) }
    } else {
        Box {}
    }
}
```



### 4.6 任务五：处理大屏模式下的系统返回键

在大屏并排模式下，用户已经在主界面，系统返回键应当退出整个 Activity，而非返回上一页。通过 `BackHandler` 和 `Activity.finish()` 实现。

**代码片段：**

kotlin

```
val context = LocalContext.current
BackHandler { (context as Activity).finish() }
```



## 五、运行结果与测试

- **手机模拟器（Compact）**：应用保持原有单窗格交互，列表页点击进入详情页，详情页点击返回按钮或系统返回键可返回列表。
- **平板模拟器（Expanded）**：启动后左侧显示运动列表，右侧同步显示当前选中运动的详情。点击列表项，右侧详情立即更新，TopAppBar 始终显示 “Sports” 且无返回按钮。系统返回键直接退出应用。

自适应布局切换流畅，界面布局合理，符合 Material 3 设计规范。

## 六、遇到的问题与解决方法

1. **构建失败，缺失图片资源和主题文件**
    原因：代码替换过程中未保留 `res/drawable/` 和 `ui/theme/` 目录。
    解决：从原始项目仓库重新拷贝相应文件夹，重新构建成功。
2. **WindowSizeClass API 实验性注解**
    编译提示需添加 `@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)`，在 MainActivity 和 SportsApp 函数上添加后通过编译。

## 七、实验总结

通过本次实验，我成功将 Sports 应用改造为支持自适应布局的 Compose 应用。关键收获包括：

- 理解了 `WindowSizeClass` 在自适应布局中的核心作用，能够根据 `WindowWidthSizeClass` 动态改变界面结构。
- 掌握了列表-详情规范布局的实现方式，使用 `Row` + `weight` 灵活分配空间。
- 学会了如何根据布局类型调整应用栏的外观与行为，以及处理不同模式下的返回导航逻辑。
- 加深了对 Compose 声明式 UI 和状态管理的理解，将 ViewModel 与 UI 布局解耦，使代码更易于维护和扩展。

本次实验为开发面向多种设备形态的 Android 应用奠定了坚实的基础，也为后续实现更复杂的自适应交互（如 Navigation Rail、分栏导航）积累了经验。
