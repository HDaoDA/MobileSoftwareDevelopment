# Lab11 实验报告：为 Sports 应用添加大屏自适应布局
## 一、实验概述
本次实验基于 Jetpack Compose 的 `material3-window-size-class` 库，为 Sports 运动资讯应用实现大屏自适应布局。核心目标是通过检测设备窗口尺寸类别，在手机等紧凑宽度设备上保留原有的“列表→详情”单窗格导航，在平板等展开宽度设备上实现“列表+详情”双窗格并排布局，提升大屏设备的用户体验。

## 二、核心概念说明
### 1. WindowSizeClass 概念
`WindowSizeClass` 是 Jetpack Compose 提供的用于适配不同屏幕尺寸的工具类，它将设备窗口尺寸划分为预定义的类别，简化了自适应布局的开发：
- `WindowWidthSizeClass`（宽度维度）包含三类：
  - `Compact`：紧凑宽度，典型为手机竖屏（宽度 < 600dp）；
  - `Medium`：中等宽度，典型为手机横屏或小平板（600dp ≤ 宽度 < 840dp）；
  - `Expanded`：展开宽度，典型为平板或大屏设备（宽度 ≥ 840dp）。
- 本次实验以 `WindowWidthSizeClass` 为核心判断依据，仅对 `Expanded` 宽度采用双窗格布局。

### 2. SportsContentType 枚举设计思路
`utils/WindowStateUtils.kt` 中定义的 `SportsContentType` 枚举包含 `ListOnly`（仅列表）和 `ListAndDetail`（列表+详情）两种类型，设计思路如下：
- 作为布局状态的“抽象标识”，解耦“尺寸判断”和“布局渲染”逻辑：无需在多处重复写 `windowWidthSizeClass == Expanded` 的判断，只需通过枚举值切换布局；
- 符合“单一职责”原则：尺寸判断只负责生成枚举值，布局渲染只根据枚举值执行对应逻辑；
- 扩展性强：若后续需新增布局类型（如“仅详情”），只需扩展枚举值，无需大范围修改代码。

## 三、自适应布局设计说明
### 1. SportsListAndDetails 双窗格布局设计
#### 核心结构
采用 `Row` 作为根布局，通过 `Modifier.weight()` 分配列表和详情的宽度比例，核心代码逻辑如下：
- 左侧列表占 1 份权重（`Modifier.weight(1f)`），右侧详情占 2 份权重（`Modifier.weight(2f)`），符合 Material 3 “列表-详情”模式的视觉比例（列表窄、详情宽）；
- 列表点击事件仅更新“当前选中运动”，不触发页面跳转（因为详情已在右侧实时显示）；
- 详情页的返回按钮在双窗格模式下置为空实现（无需返回，列表始终可见）。

#### 比例分配理由
- 列表作为“导航入口”，无需占用过多空间，1/3 宽度足够展示运动名称和简要描述；
- 详情页是核心内容展示区域，2/3 宽度能完整显示运动名称、运动员数、详细描述等信息，符合用户“浏览详情为主、切换列表为辅”的使用习惯；
- 权重比例是弹性布局，适配不同尺寸的大屏设备（如 8 寸平板和 10 寸平板），无需硬编码宽度值。

### 2. SportsAppBar 大屏/小屏适配设计
| 布局模式       | 标题显示规则                          | 返回按钮显隐规则                  |
|----------------|---------------------------------------|-----------------------------------|
| 小屏（ListOnly）| 列表页显示“Sports”，详情页显示“Sport Info” | 列表页隐藏，详情页显示            |
| 大屏（ListAndDetail） | 固定显示“Sports”                      | 始终隐藏（无返回需求）|

#### 设计考虑
- 大屏模式下，用户始终能看到列表和详情，无需通过标题区分“当前页面”，固定显示应用名称“Sports”更符合用户认知；
- 小屏模式下，标题随页面切换（列表/详情），帮助用户明确当前所处页面；
- 返回按钮仅在“小屏详情页”显示：大屏无返回场景，小屏详情页需要返回列表，符合移动端导航习惯。

### 3. 返回键行为处理策略
#### 小屏模式（ListOnly）
返回键逻辑为“详情页→列表页”：通过 `viewModel.updateIsShowingListPage(true)` 切换回列表状态，符合手机用户“点击返回→回到上一级”的操作直觉。

#### 大屏模式（ListAndDetail）
返回键逻辑为“退出应用”：通过 `(context as Activity).finish()` 关闭 Activity，理由如下：
- 大屏模式下无“页面层级”（列表和详情同时显示），不存在“返回上一级”的场景；
- 符合平板设备的交互习惯：用户按系统返回键时，期望退出应用而非无意义的“返回”；
- 避免用户在大屏模式下按返回键无响应，提升交互一致性。

## 四、核心实现步骤
### 1. MainActivity 中传递 WindowSizeClass
- 导入 `calculateWindowSizeClass` 等实验性 API，添加 `@OptIn` 注解；
- 计算 `windowSizeClass = calculateWindowSizeClass(activity = this)`，提取 `widthSizeClass` 并传入 `SportsApp`；
- 完成“尺寸检测→传递给Compose根组件”的链路。

### 2. SportsApp 核心逻辑
- 根据 `widthSizeClass` 生成 `SportsContentType` 枚举值；
- 封装列表点击事件：小屏模式下点击列表项切换到详情页，大屏模式下仅更新选中项；
- 封装返回逻辑：根据布局模式区分“返回列表”或“退出应用”；
- 根据 `SportsContentType` 切换“单窗格/双窗格”布局渲染。

### 3. 双窗格布局与 AppBar 适配
- 实现 `SportsListAndDetails` 组合项，完成 Row 布局和权重分配；
- 修改 `SportsAppBar`，新增 `isListAndDetail` 参数，控制标题和返回按钮显隐；
- 为大屏模式添加 `BackHandler`，拦截系统返回键并执行退出应用逻辑。

## 五、实验中遇到的问题与解决过程
### 问题 1：大屏模式下详情页内容padding重复
- 现象：大屏模式下详情页内容存在双重 padding（Scaffold 的 innerPadding + 自身 padding），导致内容偏移；
- 解决：在 `SportsListAndDetails` 中为列表和详情传入 `PaddingValues(0.dp)`，仅保留自身内部的 16dp padding，避免重复叠加。

### 问题 2：小屏模式点击列表项不跳转详情页
- 现象：初始代码中未区分布局模式，大屏模式的点击逻辑覆盖了小屏逻辑；
- 解决：在 `onSportClick` 中添加判断：仅当 `contentType == ListOnly` 时，才调用 `viewModel.updateIsShowingListPage(false)` 切换到详情页。

### 问题 3：大屏模式下返回键无响应
- 现象：未添加 `BackHandler` 拦截返回键，系统默认返回键无行为；
- 解决：在 `SportsApp` 的双窗格分支中添加 `BackHandler { onBackPressed() }`，绑定“退出应用”的逻辑。

### 问题 4：枚举值大小写错误导致布局切换失败
- 现象：代码中误写 `Listonly`（小写 o），导致与定义的 `ListOnly` 不匹配，大屏始终显示单窗格；
- 解决：统一枚举值书写规范，全部使用 `SportsContentType.ListOnly`/`ListAndDetail`，避免大小写错误。

## 六、实验总结
本次实验完成了 Sports 应用的大屏自适应布局改造，核心收获如下：
1. 掌握了 `WindowSizeClass` 的使用方式，能够基于窗口宽度类别实现布局切换；
2. 理解了 Material 3 “列表-详情”模式的设计原则，学会通过 `Row + Modifier.weight()` 实现弹性并排布局；
3. 学会了根据布局模式适配导航栏、返回键等交互元素，提升跨设备的交互一致性；
4. 体会到“状态抽象（枚举）”在解耦代码中的作用，让布局逻辑更清晰、易维护。

适配后的应用在手机上保留原有流畅的单窗格导航，在平板上充分利用屏幕空间，实现列表和详情的并排显示，符合不同设备的用户体验规范，达到了实验的核心目标。