# Lab11 为 Sports 应用添加大屏自适应布局 实验报告
## 一、实验基本信息
- 实验名称：基于 Jetpack Compose 实现 Sports 应用大屏自适应布局
- 学号姓名：2025003022 柳航
- 开发工具：Android Studio 64 位
- 开发语言：Kotlin
- 核心技术：WindowSizeClass、Compose 自适应布局、List-Detail 布局
- 实验目标：实现手机单栏、平板双栏自适应界面，提升大屏设备使用体验

## 二、实验核心知识点
### 1. WindowSizeClass 与屏幕宽度分类
WindowSizeClass 是 Jetpack Compose 提供的屏幕尺寸判断工具，根据设备宽度自动分类：
- **Compact**：宽度 < 600dp，适用于手机竖屏，使用单页跳转布局
- **Medium**：600dp ≤ 宽度 < 840dp，适用于折叠屏、小平板
- **Expanded**：宽度 ≥ 840dp，适用于大屏平板，使用双栏并排布局

本实验通过 calculateWindowSizeClass() 获取屏幕宽度，实现布局自动切换。

### 2. SportsContentType 枚举作用
定义两种布局模式，统一管理界面展示方式：
- **ListOnly**：小屏设备，列表与详情分开显示，通过页面跳转切换
- **ListAndDetail**：大屏设备，列表与详情同时并排展示
使用枚举可提高代码可读性，避免硬编码，便于维护与扩展。

### 3. 双栏布局 SportsListAndDetails 实现
- 使用 Row 实现左右并排布局
- 左侧列表权重 weight(1f)，右侧详情权重 weight(2f)，比例 1:2
- 点击左侧列表，右侧详情实时更新，无需页面跳转
- 仅在 Expanded 大屏模式下自动启用

### 4. TopAppBar 大屏/小屏适配逻辑
- 小屏模式：列表页标题为 Sports，详情页标题为 Sport Info，并显示返回按钮
- 大屏模式：标题固定为 Sports，始终不显示返回按钮
通过布局类型判断，动态控制标题与按钮显隐，符合 Material 3 设计规范。

### 5. 返回键 BackHandler 处理逻辑
- 小屏详情页：返回键回到列表页
- 大屏双栏模式：返回键直接退出应用
使用 BackHandler 拦截系统返回事件，实现不同屏幕的差异化行为。

## 三、实验实现步骤
### 任务1：打开并运行起始项目
使用 Android Studio 打开 basic-android-kotlin-compose-training-sports 项目，等待 Gradle 同步完成，运行项目验证列表与详情基础功能。

### 任务2：MainActivity 计算窗口尺寸
在 MainActivity 中调用 calculateWindowSizeClass() 获取设备宽度类别，将宽度参数传递给 SportsApp，实现屏幕尺寸感知。

### 任务3：SportsApp 实现自适应逻辑
根据屏幕宽度判断布局模式：Expanded 宽度使用双栏布局，其余宽度使用单栏布局，结合 SportsContentType 管理界面状态。

### 任务4：实现双栏布局 SportsListAndDetails
使用 Row 组合列表与详情界面，设置权重比例，实现点击列表实时刷新详情内容。

### 任务5：适配顶部导航栏
根据屏幕尺寸与当前页面，动态修改标题文字与返回按钮显示状态。

### 任务6：处理返回键逻辑
小屏返回列表，大屏直接退出应用，通过 BackHandler 完成差异化拦截。

### 任务7：测试与截图
在手机模拟器测试单栏跳转逻辑，在平板模拟器测试双栏布局，并完成界面截图。

## 四、实验结果
1. 手机设备正常显示列表，点击可进入详情页，返回按钮可用
2. 平板设备自动切换为左右双栏布局，点击列表实时更新详情
3. 顶部标题与返回按钮可根据屏幕大小自动适配
4. 返回键行为符合预期，界面无错乱、无报错
5. 深浅色模式均正常显示

## 五、实验问题与解决方法
### 问题1：Android resource linking failed 资源链接错误
原因：项目启动图标文件损坏
解决方法：删除 AndroidManifest.xml 中的图标引用，使用系统默认图标绕过报错。

### 问题2：代码出现红色波浪线，包导入缺失
原因：依赖未完整导入，代码结构不规范
解决方法：补全导入语句，修正代码格式，同步 Gradle 项目。

### 问题3：大屏模式下返回键回到列表页
原因：未正确拦截返回事件
解决方法：使用 BackHandler + Activity.finish() 直接退出应用。

### 问题4：双栏布局显示比例不协调
原因：未使用权重分配空间
解决方法：使用 Modifier.weight() 设置 1:2 比例，优化界面布局。

## 六、实验总结
本次实验成功完成了 Sports 应用的大屏自适应布局开发，实现了手机与平板设备的界面自动适配。
通过 WindowSizeClass 判断屏幕尺寸，结合 Compose 布局、状态管理、返回键拦截等技术，完成了老师要求的全部功能。
实验提升了自适应布局开发能力，掌握了 Material 3 大屏设计规范，能够独立完成 Android 应用多设备适配开发。
