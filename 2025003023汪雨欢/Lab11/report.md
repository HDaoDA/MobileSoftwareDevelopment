# Lab11 实验报告

## 1. WindowSizeClass 概念
WindowSizeClass 是 Jetpack Compose 提供的屏幕尺寸分类工具，用于让应用根据屏幕宽度/高度自动切换布局。

WindowWidthSizeClass 分为三类：
- Compact：手机竖屏（小屏）
- Medium：折叠屏、小平板
- Expanded：平板、横屏大手机（大屏）

## 2. SportsContentType 设计思路
使用 ListOnly 和 ListAndDetail 两种类型：
- ListOnly：小屏设备，只能显示列表或详情其中一个
- ListAndDetail：大屏设备，同时显示列表+详情
目的：让代码逻辑清晰，方便根据屏幕类型切换 UI 模式。

## 3. SportsListAndDetails 布局说明
使用 Row 实现左右并排：
- 左侧列表 weight(1f) → 占 1/3 宽度
- 右侧详情 weight(2f) → 占 2/3 宽度
设计理由：列表只需要展示文字，详情需要更大空间展示内容，符合阅读习惯。

## 4. SportsAppBar 行为差异
- 大屏模式：永远显示 Sports，不显示返回键（因为列表始终可见）
- 小屏模式：列表页显示 Sports，详情页显示 Sport Info 并显示返回键
符合用户直觉，避免大屏出现无用的返回按钮。

## 5. 返回键处理策略
- 小屏：返回键用于从详情页回到列表页
- 大屏：已经同时显示列表+详情，返回键应直接退出应用
保证不同设备的返回行为符合系统规范。

## 6. 遇到的问题与解决
1. 大屏返回键无法退出 → 使用 BackHandler + finish() 解决
2. 顶部栏在大屏仍显示返回键 → 增加 isListAndDetail 参数控制
3. 点击列表不刷新详情 → 直接使用 viewModel.updateCurrentSport 即可