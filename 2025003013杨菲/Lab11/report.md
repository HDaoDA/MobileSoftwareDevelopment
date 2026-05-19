# Lab11：为 Sports 应用添加大屏自适应布局

## 1. WindowSizeClass 概念简介
WindowSizeClass 是 Jetpack Compose 中用于检测设备窗口尺寸的 API，通过 calculateWindowSizeClass() 获取当前窗口大小类别。WindowWidthSizeClass 有三种宽度类别：
- Compact（紧凑）：宽度 < 600dp，适用于手机竖屏
- Medium（中等）：600dp ≤ 宽度 < 840dp，适用于手机横屏或折叠屏展开
- Expanded（展开）：宽度 ≥ 840dp，适用于平板等大屏设备

## 2. SportsContentType 枚举设计思路
SportsContentType 定义了 ListOnly 和 ListAndDetail 两种类型。这样设计可以将屏幕尺寸与 UI 结构解耦，当窗口尺寸变化时，只需改变 contentType 即可自动切换布局，无需修改大量业务代码。

## 3. SportsListAndDetails 布局设计
使用 Row 布局实现并排显示，列表占 weight(1f)（约1/3宽度），详情占 weight(2f)（约2/3宽度）。比例分配为1:2的原因是列表只需要足够显示项目信息，而详情需要更多空间展示图片和文字。

## 4. SportsAppBar 行为差异设计
- 大屏模式（ListAndDetail）：标题固定显示"Sports"，不显示返回按钮
- 小屏列表页：标题显示"Sports"，不显示返回按钮
- 小屏详情页：标题显示"Sport Info"，显示返回按钮
这样设计是因为大屏下列表始终可见，无需返回按钮；小屏下详情页需要返回按钮回到列表。

## 5. 返回键处理策略
- 大屏模式：列表和详情同时显示，按返回键退出应用
- 小屏列表页：按返回键退出应用
- 小屏详情页：按返回键返回列表页
大屏模式使用 BackHandler 拦截返回键并调用 finish() 退出应用。

## 6. 实验中遇到的问题与解决
- 问题1：缺少 theme 文件夹和 SportsTheme → 手动创建 Color.kt、Type.kt、Theme.kt
- 问题2：缺少字符串资源 → 在 strings.xml 中添加 sports_list_subtitle、sport_detail_text 等
- 问题3：缺少运动图片资源 → 使用 Android 系统内置图标代替
- 问题4：TopAppBar 实验性 API 警告 → 添加 @OptIn(ExperimentalMaterial3Api::class) 注解

## 实验总结
本次实验成功为 Sports 应用添加了大屏自适应布局，实现了根据窗口尺寸自动切换单窗格/双窗格布局的功能。
