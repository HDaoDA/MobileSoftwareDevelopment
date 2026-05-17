
# Lab10 实验报告：为 Lunch Tray 添加导航

## 1. Compose Navigation 中 NavController、NavHost 和 composable() 的关系简述

- **NavController**：导航控制器，负责管理导航堆栈、处理页面跳转和返回逻辑，是整个导航系统的“大脑”。
- **NavHost**：导航宿主，是页面内容的容器，负责在屏幕上显示当前路由对应的页面，由 NavController 控制切换。
- **composable()**：NavHost 内部的路由声明函数，用于注册每一个页面及其对应的 Composable 界面，定义“路由名称 → 页面内容”的映射关系。

三者关系：**NavController 控制 NavHost，NavHost 通过 composable() 注册并渲染不同页面**，共同完成多页面导航。

## 2. LunchTrayScreen 枚举类的设计说明

本次实验定义了 `LunchTrayScreen` 枚举类，包含 Start、Entree、SideDish、Accompaniment、Checkout 五个页面，并为每个页面绑定对应的标题资源 ID。

选择**枚举而非直接字符串**的原因：
1. **编译期安全**：枚举在编译时校验，避免字符串拼写错误；裸字符串写错会运行时崩溃。
2. **集中管理**：所有页面名称、标题资源统一放在一处，便于维护。
3. **语义清晰**：使用 `LunchTrayScreen.Entree.name` 比直接写 `"Entree"` 可读性更强。
4. **扩展性好**：新增页面只需添加枚举项，无需修改大量字符串常量。

## 3. LunchTrayAppBar 的设计思路

`LunchTrayAppBar` 是一个可复用的顶部导航栏组件，核心设计如下：

1. **动态标题**：根据当前页面 `currentScreen`，通过 `stringResource` 显示对应页面标题。
2. **返回按钮**：使用箭头图标，点击调用 `navigateUp()` 返回上一页。
3. **显示条件**：
   - 当 `navController.previousBackStackEntry != null` 时显示返回按钮；
   - Start 页面无前置页面，不显示返回按钮，符合用户直觉。
4. **样式统一**：使用 Material 3 主题配色，保持界面一致性。

## 4. 导航流程设计说明（含返回堆栈管理）

### 导航流程
- Start → Entree → SideDish → Accompaniment → Checkout
- 任意页面点击 Cancel → 回到 Start 并清空订单
- Checkout 点击 Submit → 回到 Start 并重置订单

### 返回堆栈管理关键设计
1. **进入点餐流程时弹出 Start**
   ```kotlin
   navController.navigate(Entree) {
       popUpTo(Start) { inclusive = true }
   }
   ```
   原因：用户从 Start 进入点餐流程后，**按系统返回键应直接退出应用**，而非回到空白的 Start 页，符合常规 App 体验。

2. **Cancel/Submit 时清空整个堆栈返回 Start**
   ```kotlin
   navController.navigate(Start) {
       popUpTo(startDestinationId) { inclusive = true }
   }
   ```
   原因：取消或提交订单后，应**重置整个点餐流程**，返回初始状态，避免返回堆栈残留旧页面。

## 5. 实验中遇到的问题与解决过程

1. **问题：按返回键会回到 Start 页面，而非退出应用**
   - 原因：Start 页面仍在返回堆栈中。
   - 解决：从 Start 跳转到 Entree 时，使用 `popUpTo(Start) { inclusive = true }` 将 Start 从堆栈中移除。

2. **问题：Cancel 后订单状态未清空，再次点餐会保留旧选择**
   - 原因：仅导航返回 Start，未调用 ViewModel 重置订单。
   - 解决：封装 `cancelAndReset` 函数，取消时同时调用 `viewModel.resetOrder()`。

3. **问题：返回按钮在 Start 页面也显示**
   - 原因：判断条件错误。
   - 解决：使用 `navController.previousBackStackEntry != null` 判断是否显示返回按钮，Start 页面无前置页面，不显示。

4. **问题：路由名称和枚举名称不一致，运行时崩溃**
   - 原因：路由字符串拼写错误。
   - 解决：统一使用 `LunchTrayScreen.xxx.name` 作为路由，避免硬编码字符串。
