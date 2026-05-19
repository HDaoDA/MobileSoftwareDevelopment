# Lab11 实验报告：为 Sports 应用添加大屏自适应布局

## 1. WindowSizeClass 概念简介

`WindowSizeClass` 是 Material 3 提供的窗口尺寸分类工具，用来根据当前应用窗口大小判断界面应该采用哪一种布局。相比直接读取像素宽高，`WindowSizeClass` 更适合自适应界面设计，因为它把屏幕宽度和高度归纳成几个稳定的类别，便于在手机、折叠屏、平板和桌面窗口之间切换布局。

本实验主要使用 `WindowWidthSizeClass`，它有三种常见宽度类别：

- `Compact`：紧凑宽度，通常对应竖屏手机，适合单窗格布局。
- `Medium`：中等宽度，通常对应横屏手机、小平板或部分折叠屏，仍可继续使用单窗格布局。
- `Expanded`：展开宽度，通常对应平板、桌面窗口或大屏设备，适合使用列表与详情并排显示的双窗格布局。

在本实验中，应用入口 `MainActivity` 使用 `calculateWindowSizeClass(activity = this)` 计算当前窗口尺寸，并把 `widthSizeClass` 传递给 `SportsApp`，由 `SportsApp` 决定具体显示方式。

## 2. SportsContentType 枚举设计思路

项目中已经提供了 `SportsContentType` 枚举：

```kotlin
enum class SportsContentType {
    ListOnly, ListAndDetail
}
```

该枚举用于把“窗口尺寸判断结果”和“页面具体布局”分离开来。`SportsApp` 只需要根据 `WindowWidthSizeClass` 得出当前内容类型：

- `ListOnly`：只显示列表页或详情页中的一个，适合手机等小屏设备。
- `ListAndDetail`：同时显示列表和详情，适合平板等大屏设备。

这样做的好处是代码更清晰。后续如果需要让 `Medium` 也显示双窗格，或者为折叠屏添加新的布局，只需要修改内容类型判断逻辑，不需要大范围改动 UI 组件。

## 3. SportsListAndDetails 布局设计说明

本实验新增了 `SportsListAndDetails` 组件，用于大屏模式下的列表-详情布局。该组件使用 `Row` 让两个区域水平排列：

- 左侧调用 `SportsList` 显示运动列表。
- 右侧调用 `SportsDetail` 显示当前选中运动的详情。

布局比例使用 `Modifier.weight()` 分配：

```kotlin
SportsList(..., modifier = Modifier.weight(1f))
SportsDetail(..., modifier = Modifier.weight(2f))
```

这样列表占约三分之一宽度，详情占约三分之二宽度。列表区域只需要展示运动名称、简介和基本信息，因此不需要太宽；详情区域需要展示大图和较长文本，所以分配更多空间更合理。

在大屏模式下，点击左侧列表项时，只调用 `viewModel.updateCurrentSport(it)` 更新当前选中的运动，不再跳转页面。右侧详情会根据 `currentSport` 状态自动刷新，实现列表和详情同步显示。

## 4. SportsAppBar 在大屏和小屏下的行为差异

小屏模式使用单窗格导航，因此应用栏保持原有行为：

- 列表页标题显示 `Sports`。
- 详情页标题显示 `Sport Info`。
- 详情页显示返回按钮，点击后返回列表页。

大屏模式下，列表和详情始终同时显示，用户没有进入一个独立的详情页面，因此应用栏做了不同处理：

- 标题始终显示 `Sports`。
- 不显示返回按钮。

这样的设计符合大屏列表-详情布局的使用习惯：左侧列表本身就是导航入口，右侧详情只是当前选中内容，不需要通过返回按钮回到列表。

## 5. 返回键处理策略

小屏模式下，列表页和详情页是二选一显示的。当用户在详情页按系统返回键时，应该回到列表页，所以 `SportsDetail` 中保留 `BackHandler`，调用 `viewModel.navigateToListPage()`。

大屏模式下，列表和详情已经同时显示，当前界面就是主界面。此时按系统返回键不应该“返回列表”，而应该退出应用。因此在 `SportsListAndDetails` 中添加了 `BackHandler`，通过当前 `Activity` 的 `finish()` 结束应用。

同时，为了避免右侧 `SportsDetail` 自己的返回键处理器拦截大屏返回事件，给 `SportsDetail` 增加了 `isBackHandlerEnabled` 参数。在小屏详情页中启用该参数，在大屏双窗格模式下禁用它，由 `SportsListAndDetails` 统一处理返回键。

## 6. 实验中遇到的问题与解决过程

实验中主要遇到的问题是：原来的 `SportsDetail` 内部已经写了 `BackHandler`。如果在大屏布局中直接复用 `SportsDetail`，右侧详情页可能会拦截系统返回键，导致 `SportsListAndDetails` 中的退出逻辑无法生效。

解决方法是为 `SportsDetail` 增加 `isBackHandlerEnabled` 参数：

- 小屏详情页：保持默认值 `true`，按返回键回到列表页。
- 大屏双窗格页：传入 `false`，禁用详情页自己的返回处理，让外层 `SportsListAndDetails` 接管返回键并退出应用。

另一个需要注意的问题是：大屏模式下点击列表项不能再调用 `navigateToDetailPage()`，否则会把单窗格状态和双窗格状态混在一起。因此在 `ListAndDetail` 模式下，点击列表只更新当前运动，不改变 `isShowingListPage`。

## 7. 验证说明

完成代码后，应在 Android Studio 中分别使用手机模拟器和平板/可调整大小模拟器验证：

- 手机宽度下，列表页点击项目后进入详情页。
- 手机详情页显示返回按钮，点击返回按钮或系统返回键可回到列表页。
- 平板宽度下，列表和详情并排显示。
- 平板宽度下，点击左侧列表项，右侧详情同步更新。
- 平板宽度下，应用栏标题始终为 `Sports`，不显示返回按钮。
- 平板宽度下，按系统返回键退出应用。
