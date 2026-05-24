
# Lab11 Sports 自适应布局实验报告

## 1. WindowSizeClass 概念简介及 WindowWidthSizeClass 三种宽度类别

### WindowSizeClass 概念
`WindowSizeClass` 是 Jetpack 提供的**窗口尺寸分类工具**，用于在运行时根据屏幕宽度/高度把设备分成不同尺寸类别，从而实现**响应式布局**：同一套代码自动适配手机、平板、折叠屏、桌面设备。

### WindowWidthSizeClass 三种宽度类别
- **Compact（紧凑）**：宽度 < 600dp  
  适用：**手机竖屏**。
- **Medium（中等）**：600dp ≤ 宽度 < 840dp  
  适用：**手机横屏、小平板**。
- **Expanded（展开）**：宽度 ≥ 840dp  
  适用：**大屏平板、折叠屏展开态、桌面设备**。

在本项目中：
- Compact / Medium → `ListOnly` 模式（单页）
- Expanded → `ListAndDetail` 模式（双面板）

---

## 2. SportsContentType 枚举的设计思路

### 设计思路
页面内容形态由**屏幕宽度**决定：
- 小屏空间有限：只能**单页展示列表或详情**。
- 大屏空间充足：可以**列表+详情并排**，提升效率、减少跳转。

### 为什么使用 `ListOnly` 和 `ListAndDetail` 两种类型
- **覆盖主流设备形态**：手机使用 `ListOnly`，平板/大屏使用 `ListAndDetail`。
- **逻辑清晰、易维护**：UI 分支只有两条，ViewModel 只需控制“是否显示详情页”。
- **避免过度设计**：无需为 Compact/Medium/Expanded 各做一套，降低维护成本。

---

## 3. SportsListAndDetails 的布局设计说明

### 布局结构
大屏时使用 `Row` 水平排列：
```kotlin
Row {
    SportsList( modifier = Modifier.weight(2f) )
    SportsDetail( modifier = Modifier.weight(3f) )
}
```

### 比例分配理由（2f : 3f）
- **列表区（2 份）**：作为导航入口，不需要太宽，保证可浏览即可。
- **详情区（3 份）**：内容区（图文、长文本）需要更大空间保证可读性。
- **视觉平衡**：列表偏窄、详情偏宽，符合用户“左导航、右内容”的阅读习惯。
- **适配大屏设备**：比例固定，不同大屏宽度下都能保持合理分区。

---

## 4. SportsAppBar 在大屏/小屏下行为差异的设计考虑

### 小屏（Compact/Medium）
- 标题：根据当前页面状态显示“列表页”或“详情页”。
- 返回按钮：仅在详情页显示，点击返回列表页。
- 逻辑：小屏为**栈式导航**，需要页面跳转与返回。

### 大屏（Expanded）
- 标题：固定显示“Sports”或“列表页”，不随选中项变化。
- 返回按钮：始终隐藏。
- 逻辑：大屏为**双面板同屏**，无需页面跳转，避免用户困惑。

---

## 5. 返回键的处理策略：小屏和大屏模式下的差异

### 小屏模式（ListOnly）
- 列表页：按返回键直接退出应用。
- 详情页：按返回键回到列表页。
- 实现：通过 `BackHandler` 拦截返回事件，调用 ViewModel 导航回列表页。

### 大屏模式（ListAndDetails）
- 行为：按返回键直接退出整个 Activity。
- 实现：在 `SportsListAndDetails` 外层添加 `BackHandler`，直接调用 `onBackPressed()` 或 `finish()`。
- 原因：大屏模式下列表与详情同屏显示，无页面栈，按返回键应直接退出应用，而非回到某个“上一页”。

---

## 6. 实验中遇到的问题与解决过程

### 问题 1：大屏模式下按返回键无法退出 Activity
- 现象：平板双面板模式下，按系统返回键无反应，或仍停留在应用内。
- 原因：未在 `SportsListAndDetails` 中添加 `BackHandler`，返回事件透传给 Activity 时，默认行为不符合题目要求。
- 解决：在 `SportsListAndDetails` 外层添加 `BackHandler`，直接调用 `onBackPressed()` 或 `finish()`，确保按返回键直接退出 Activity。

### 问题 2：大屏切换运动后，标题错误显示为“详情页”
- 现象：平板选中运动后，TopAppBar 标题变为详情页。
- 原因：标题逻辑依赖 `isShowingListPage`，大屏下该状态被误更新。
- 解决：大屏模式强制显示列表页标题，忽略页面状态。

### 问题 3：大屏 Row 比例不合理，详情区内容拥挤
- 现象：大屏列表过宽、详情过窄，文字排版拥挤。
- 原因：初始使用 1:1 或 1:2 比例，不符合阅读优先级。
- 解决：调整比例为 2:3，优先保证详情区空间。

### 问题 4：手机横屏（Medium）错误进入双面板模式
- 现象：手机横屏（600dp+）布局错乱，误展示 ListAndDetail。
- 原因：WindowWidthSizeClass 判断逻辑错误，将 Medium 归为 Expanded。
- 解决：严格区分尺寸类别，Compact/Medium 使用 ListOnly，Expanded 使用 ListAndDetail。
```

---

## 二、大屏返回键退出 Activity 的代码修改

在你的 `SportsListAndDetails` 函数里加上 `BackHandler`：

```kotlin
@Composable
fun SportsListAndDetails(
    sports: List<Sport>,
    selectedSport: Sport?,
    onSportSelected: (Sport) -> Unit,
    onBackPressed: () -> Unit // 新增
) {
    // 关键：给整个大屏布局加上返回处理
    BackHandler {
        onBackPressed() // 调用后退出 Activity
    }

    Row {
        SportsList(
            sports = sports,
            selectedSport = selectedSport,
            onSportSelected = onSportSelected,
            modifier = Modifier.weight(2f)
        )
        if (selectedSport != null) {
            SportsDetail(
                sport = selectedSport,
                modifier = Modifier.weight(3f)
            )
        }
    }
}
```

在调用处传入 `onBackPressed`：

```kotlin
SportsListAndDetails(
    sports = sports,
    selectedSport = selectedSport,
    onSportSelected = { viewModel.selectSport(it) },
    onBackPressed = { activity?.finish() } // 这里直接 finish 掉 Activity
)
```

---

