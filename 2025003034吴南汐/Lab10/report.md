# 为 Lunch Tray 添加导航

## 1. Compose Navigation 中 NavController、NavHost 和 composable() 三者之间的关系简述
- **NavController**：导航的**控制中心**，负责页面跳转、返回、管理返回栈、获取当前页面状态，是所有导航行为的执行者。
- **NavHost**：导航的**容器**，用来承载所有可跳转页面，接收 NavController 并管理整个导航图。
- **composable()**：导航图中的**路由项**，为每个页面配置唯一路由，绑定对应 UI 组件。

三者关系：**NavController 指挥，NavHost 提供容器，composable() 定义页面路由**，共同完成多页面切换与状态管理。

---

## 2. LunchTrayScreen 枚举类的设计说明（为什么使用枚举而不是直接用字符串）
本次实验使用 `enum class LunchTrayScreen` 统一管理页面路由与标题，相比直接使用字符串有明显优势：
1. **类型安全**：路由名称是编译期常量，不会因拼写错误导致路由失效。
2. **集中管理**：页面标题、路由名统一维护，便于扩展与修改。
3. **语义清晰**：代码可读性更高，一眼可知页面含义。
4. **避免硬编码**：减少散落的字符串常量，提升可维护性。

因此使用枚举是 Compose 导航中**标准、推荐**的页面管理方式。

---

## 3. LunchTrayAppBar 的设计思路，包括返回按钮的显示条件判断
LunchTrayAppBar 是应用统一顶部栏，核心设计如下：
1. **动态标题**：根据当前页面枚举，读取对应字符串资源，自动切换标题。
2. **返回按钮逻辑**：
   - 显示条件：`navController.previousBackStackEntry != null`
   - 含义：**只有存在上一个页面时才显示返回按钮**。
   - 效果：Start 页面不显示返回箭头，其他页面正常显示。
3. **样式统一**：使用 Material3 TopAppBar，配色与主题保持一致。

---

## 4. 导航流程的设计说明，特别是返回堆栈管理的考虑
### 导航流程
Start → Entree（口味）→ SideDish（日期）→ Checkout（订单摘要）

### 返回堆栈管理（重点）
从 **Start 页面进入点餐流程时**，执行以下配置：
```kotlin
popUpTo(LunchTrayScreen.Start.name) { inclusive = true }
```
**原因：**
- 业务上，点餐流程是**单向线性流程**。
- 用户进入选单后，按系统返回键应**直接退出应用**，而不是回到 Start 页。
- 弹出 Start 页可避免用户重复看到开始页面，提升体验。

Cancel 操作同样清空返回栈回到 Start，保证页面状态干净。

---

## 5. 实验中遇到的问题与解决过程
1. **问题：Unresolved reference: fillMaxWidth / padding**
   原因：未导入 Compose 布局包
   解决：在文件顶部添加 `import androidx.compose.foundation.layout.*`

2. **问题：返回按钮在 Start 页面依然显示**
   原因：未正确判断返回栈
   解决：使用 `previousBackStackEntry != null` 控制显示

3. **问题：导航后标题不更新**
   原因：未监听返回栈变化
   解决：使用 `currentBackStackEntryAsState()` 实时获取当前页面

4. **问题：Cancel 后订单数据未清空**
   原因：只跳转页面未调用 ViewModel
   解决：跳转同时执行 `viewModel.resetOrder()`

5. **问题：从点餐页返回时回到 Start 页**
   原因：未配置 popUpTo
   解决：导航时弹出 Start 页面，保证返回键直接退出应用