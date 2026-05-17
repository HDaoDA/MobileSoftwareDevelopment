# Lab9：为 Dessert Clicker 添加 ViewModel 实验报告

## 一、实验概述

### 1. 实验背景

本次实验基于 Jetpack Compose 与 ViewModel 架构组件，对 Dessert Clicker（甜品点击器）应用进行优化与重构。原有项目将状态管理、数据处理以及业务逻辑全部写在 `MainActivity` 的可组合函数中，导致 UI 与逻辑耦合严重，代码可读性和后期维护性较差。

为了提升项目结构的规范性，本实验引入 ViewModel 组件，对应用进行 MVVM 架构改造，实现界面层与业务逻辑层的分离。

### 2. 实验目标

1. 理解 ViewModel 在 Android 架构中的作用；
2. 使用 `DessertUiState` 统一管理页面状态；
3. 创建 `DessertViewModel` 负责业务逻辑处理；
4. 重构 `MainActivity`，降低 UI 与逻辑耦合；
5. 验证应用在屏幕旋转后的状态保持能力。

------

## 二、ViewModel 在 Android 架构中的作用

ViewModel 是 Jetpack 提供的重要架构组件，其主要功能如下：

1. **管理界面状态**
   ViewModel 可以保存界面数据，在屏幕旋转等配置变更时不会销毁，从而避免数据丢失。
2. **实现 UI 与业务逻辑分离**
   将数据处理逻辑从 Activity 或 Composable 中抽离，使 UI 只负责界面展示和事件响应。
3. **提升代码可维护性**
   状态与逻辑统一管理后，代码结构更加清晰，便于后续功能扩展与维护。
4. **提高可测试性**
   ViewModel 不依赖具体 UI，可以单独进行逻辑测试。
5. **统一状态管理**
   所有界面状态集中保存在 ViewModel 中，避免状态分散造成的问题。

------

## 三、DessertUiState 数据类设计说明

`DessertUiState` 用于统一保存界面展示所需的数据状态，代码位于 `ui/DessertUiState.kt` 文件中。

### 字段设计

| 字段名                | 类型 | 默认值             | 作用说明       |
| --------------------- | ---- | ------------------ | -------------- |
| revenue               | Int  | 0                  | 当前总收入     |
| dessertsSold          | Int  | 0                  | 已售出甜品数量 |
| currentDessertIndex   | Int  | 0                  | 当前甜品索引   |
| currentDessertImageId | Int  | R.drawable.cupcake | 当前甜品图片   |
| currentDessertPrice   | Int  | 5                  | 当前甜品价格   |

### 设计思路

1. 所有字段均设置默认值，保证应用启动时能够正确初始化；
2. 使用数据类统一管理状态，结构更加清晰；
3. 借助 `copy()` 方法更新状态，能够自动触发 Compose 界面重组；
4. 使用 `@DrawableRes` 标注图片资源，提高代码规范性。

------

## 四、DessertViewModel 设计思路

`DessertViewModel` 继承自 `ViewModel`，主要负责应用中的状态管理和业务逻辑处理。

### 1. 核心成员变量

```kotlin
var uiState by mutableStateOf(DessertUiState())
    private set

private val desserts = Datasource.dessertList
```

说明：

- `mutableStateOf` 用于让 Compose 监听状态变化；
- `private set` 保证外部只能读取状态，不能直接修改；
- `desserts` 保存甜品数据源。

### 2. 核心方法设计

#### （1）onDessertClicked()

该方法用于处理甜品点击事件：

- 更新收入；
- 更新销量；
- 判断是否需要解锁新的甜品；
- 更新 UI 状态。

```kotlin
fun onDessertClicked() {
    // 更新状态逻辑
}
```

#### （2）determineDessertToShow()

该方法根据销量判断当前应该显示的甜品，实现甜品升级逻辑。

### 3. 设计优势

1. 业务逻辑集中管理；
2. 状态修改更加安全；
3. UI 层代码更加简洁；
4. 屏幕旋转时状态不会丢失。

------

## 五、MainActivity 重构前后对比分析

### 1. 重构前

原项目存在以下问题：

1. 使用多个 `rememberSaveable` 保存状态；
2. 点击逻辑直接写在 Composable 中；
3. UI 与业务逻辑混合；
4. 代码可维护性较差。

### 2. 重构后

重构后主要优化如下：

1. 所有状态统一由 ViewModel 管理；
2. UI 只负责展示数据；
3. 点击事件通过 `viewModel.onDessertClicked()` 调用；
4. 使用 `viewModel()` 获取 ViewModel 实例；
5. 配置变更后状态能够自动保存。

### 3. 核心变化

| 内容     | 重构前           | 重构后              |
| -------- | ---------------- | ------------------- |
| 状态管理 | rememberSaveable | ViewModel           |
| 业务逻辑 | 写在 UI 中       | 写在 ViewModel 中   |
| 状态更新 | 直接修改变量     | 通过 ViewModel 更新 |
| 配置变更 | 手动处理         | 自动保存            |

------

## 六、重构前后代码结构区别与体会

### 1. 代码结构区别

| 对比项     | 重构前        | 重构后    |
| ---------- | ------------- | --------- |
| 架构模式   | UI 与逻辑混合 | MVVM 分层 |
| 状态管理   | 分散          | 集中      |
| 代码可读性 | 一般          | 较高      |
| 可维护性   | 较差          | 更好      |
| 可测试性   | 较低          | 更高      |

### 2. 实验体会

通过本次实验，我对 ViewModel 的作用有了更深入的理解。相比之前将所有逻辑写在 Activity 中，使用 ViewModel 后代码结构更加清晰，功能划分更加明确。

同时，我也认识到 MVVM 架构在实际开发中的重要性。通过状态统一管理，不仅减少了代码耦合，还提升了程序稳定性。

------

## 七、实验过程中遇到的问题与解决方法

### 问题 1：viewModel() 无法导入

**原因：**

未正确添加 ViewModel Compose 依赖。

**解决方法：**

在 `build.gradle.kts` 中添加：

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
```

随后同步 Gradle。

------

### 问题 2：状态更新后界面没有刷新

**原因：**

状态未使用 `mutableStateOf` 管理。

**解决方法：**

使用：

```kotlin
var uiState by mutableStateOf(DessertUiState())
```

并通过 `copy()` 更新状态。

------

### 问题 3：Preview 预览报错

**原因：**

预览环境无法自动创建 ViewModel。

**解决方法：**

在 Preview 中手动创建 `DessertViewModel` 实例。

------

### 问题 4：屏幕旋转后数据丢失

**原因：**

部分状态仍保存在 UI 层。

**解决方法：**

将所有状态迁移到 ViewModel 中统一管理。

------

## 八、实验验证结果

经过测试，应用功能均运行正常：

1. 点击甜品后收入和销量能够正确增加；
2. 达到指定销量后自动切换高级甜品；
3. 分享功能能够正常使用；
4. 屏幕旋转后数据不会丢失；
5. 应用运行稳定，无闪退现象。

------

## 九、实验总结

本次实验通过引入 ViewModel 对 Dessert Clicker 应用进行了架构优化，实现了 UI 层与业务逻辑层的分离。

在实验过程中，我掌握了：

- ViewModel 的基本使用方法；
- Compose 中的状态管理机制；
- `mutableStateOf` 的使用方式；
- MVVM 架构思想。

通过本次实验，我进一步理解了 Android 官方推荐的现代开发架构，也为后续开发更加规范、可维护的 Android 应用积累了实践经验。