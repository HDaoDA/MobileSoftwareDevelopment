# 期末大作业报告：运动记录 (FitTrack)

## GitHub 仓库地址

GitHub 仓库地址：https://github.com/HDaoDA/CampusAssistant

（请替换为你的实际 Public 仓库地址）

---

## 基本信息

| 项目 | 内容 |
|------|------|
| **App 名称** | 运动记录 (FitTrack) |
| **开发语言** | Kotlin |
| **UI 框架** | Jetpack Compose |
| **最低版本** | Android 7.0 (API 24) |
| **目标版本** | Android 14 (API 35) |

---

## 一、选题背景与目标用户

### 选题背景

随着健康意识的提高，越来越多的人开始关注日常运动。但很多人缺乏记录习惯，不清楚自己每周运动多少次、消耗了多少热量。一个简洁的运动记录应用能帮助用户养成记录习惯，看到自己的进步轨迹。

### 目标用户

- 健身初学者，想记录每日运动
- 跑步/骑行/游泳爱好者，想统计运动数据
- 普通人群，想了解自己的运动消耗

---

## 二、核心功能说明

### 功能列表

| 功能 | 说明 |
|------|------|
| 📝 **新增运动记录** | 输入运动名称、类型、时长、卡路里、距离、备注 |
| ✏️ **编辑运动记录** | 点击已有记录可重新编辑 |
| 🗑️ **删除运动记录** | 带确认对话框的删除功能 |
| 🔍 **搜索记录** | 按运动名称或备注关键词模糊搜索 |
| 🏷️ **按类型筛选** | 筛选全部/跑步/骑行/游泳/力量/瑜伽/其他 |
| 📊 **运动统计** | 总运动次数、卡路里、时长、距离的聚合统计 |
| 🎯 **每日目标** | 设定每日卡路里目标，显示进度条 |
| 📈 **分类统计** | 按运动类型分组统计（GROUP BY） |
| 💡 **运动推荐** | 从 wger.de API 获取运动项目推荐 |
| 🏠 **最近记录** | 展示最近 5 条运动记录 |
| ⚙️ **偏好设置** | 设置默认运动类型和每日卡路里目标 |

### 用户操作流程

```
打开应用 → 点击 + 按钮 → 填写运动信息 → 保存
                                              ↓
记录列表 ← 筛选/搜索 ← 查看运动趋势 ← 统计页面
    ↓                        ↓
点击记录 → 编辑/删除      设置页面 → 修改目标
```

---

## 三、页面结构说明

应用包含 4 个页面，通过底部导航栏切换（记录 / 统计 / 设置）：

| 页面 | 路由 | 功能描述 |
|------|------|----------|
| **记录** (HomeScreen) | `home` | 运动记录列表（LazyColumn），支持筛选、搜索、FAB 新增 |
| **统计** (StatsScreen) | `stats` | 统计卡片、每日目标进度、分类统计、运动推荐、最近记录 |
| **设置** (SettingsScreen) | `settings` | 默认运动类型、每日卡路里目标设置 |
| **新增/编辑** (AddEditScreen) | `add_edit/{recordId}` | 表单页，支持新增和编辑两种模式 |

底部导航栏 3 个 Tab：
- 🏋️ 记录 → `FitnessCenter` 图标
- 📊 统计 → `BarChart` 图标
- ⚙️ 设置 → `Settings` 图标

---

## 四、技术栈说明

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **UI** | Jetpack Compose | BOM 2024.11.00 | 全部界面 |
| **UI** | Material 3 | - | 组件库（Card、Button、TextField、FAB 等） |
| **导航** | Compose Navigation | 2.8.5 | 页面路由和底部导航 |
| **架构** | ViewModel | 2.8.7 | 状态管理 |
| **数据库** | Room | 2.6.1 | 本地数据持久化 |
| **偏好** | DataStore Preferences | 1.1.1 | 用户设置存储 |
| **网络** | Retrofit + OkHttp | 2.11.0 / 4.12.0 | 网络请求 |
| **图片** | Coil | 2.7.0 | 图片加载缓存 |
| **异步** | Kotlin Coroutines + Flow | - | 异步和响应式数据流 |
| **JSON** | Gson | - | JSON 反序列化（通过 Retrofit converter） |
| **语言** | Kotlin | 2.1.0 | 开发语言 |

---

## 五、Room 数据库设计

### 表结构

**表 1：workout_records（运动记录）**

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long (PK, autoGenerate) | 主键，自增 |
| `exercise_name` | String | 运动名称 |
| `type` | String | 运动类型（跑步/骑行/游泳/力量/瑜伽/其他） |
| `duration_minutes` | Int | 运动时长（分钟） |
| `calories` | Int | 消耗卡路里（千卡） |
| `distance_km` | Float | 运动距离（公里），默认 0 |
| `date` | Long | 运动日期时间戳 |
| `notes` | String | 备注，默认空 |

**表 2：workout_goals（运动目标）**

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long (PK, autoGenerate) | 主键，自增 |
| `title` | String | 目标名称 |
| `goal_type` | String | 目标类型（WEEKLY_COUNT / DAILY_CALORIES 等） |
| `target_value` | Float | 目标值 |
| `current_value` | Float | 当前进度，默认 0 |
| `is_active` | Boolean | 是否启用，默认 true |
| `created_at` | Long | 创建时间戳 |

### 主要 DAO 查询

- `getAllRecords()`：返回 `Flow<List<WorkoutRecord>>`，按日期降序
- `getByType(type)`：按运动类型筛选
- `searchRecords(query)`：按名称/备注模糊搜索（LIKE + %）
- `getStatsByType()`：按类型分组统计（GROUP BY），返回聚合结果（次数、卡路里、时长、距离）
- `getRecentRecords(limit)`：获取最近 N 条记录
- 完整 CRUD：`insert`、`update`、`delete`

---

## 六、DataStore 设计

`UserPreferences.kt` 使用 Preferences DataStore 保存以下数据：

| Key | 类型 | 默认值 | 用途 |
|-----|------|--------|------|
| `default_type` | String | "跑步" | 默认运动类型，新增记录时自动选择 |
| `daily_calorie_goal` | String | "500" | 每日卡路里目标，统计页显示进度 |
| `is_first_launch` | Boolean | true | 是否首次启动（预留功能） |

读写场景：
- 新增运动记录页面自动选择默认类型
- 统计页面根据每日目标显示进度条
- 设置页面修改偏好后实时保存

---

## 七、网络功能设计

### API 来源

| 项目 | 内容 |
|------|------|
| **API 名称** | wger.de Exercise API |
| **接口地址** | `https://wger.de/api/v2/exercise/` |
| **认证方式** | 无需 API Key，免费开放 |
| **请求方式** | GET |

### 接口与返回字段

**接口 1：获取运动列表**
```
GET /api/v2/exercise/?language=2&limit=20
```
| 返回字段 | 说明 |
|----------|------|
| `id` | 运动项目 ID |
| `name` | 运动名称 |
| `description` | 运动描述 |
| `category` | 分类 ID |
| `muscles` | 涉及肌群 ID 列表 |
| `equipment` | 使用器械 ID 列表 |

**接口 2：搜索运动**
```
GET /api/v2/exercise/search/?term=深蹲&language=2
```

### 数据在 App 中的用途

- 统计页面的「运动推荐」区域展示来自 API 的运动项目
- 为用户提供训练灵感，了解不同运动类型
- 网络失败时自动降级为本地模拟数据，保证用户体验

### 网络架构

```
UI (Composable)
    ↓ collectAsState()
ViewModel
    ↓ suspend fun
Repository
    ↓ RetrofitClient.apiService
OkHttp (连接池 + 10s 超时)
    ↓ HTTPS
wger.de API
```

---

## 八、ViewModel 和 UiState 设计

### WorkoutViewModel

管理记录列表状态：

```kotlin
sealed interface ExerciseUiState {
    data object Idle : ExerciseUiState
    data object Loading : ExerciseUiState
    data class Success(val exercises: List<ExerciseDto>) : ExerciseUiState
    data class Error(val message: String) : ExerciseUiState
}
```

- `records: StateFlow<List<WorkoutRecord>>` — 记录列表
- `filterType: StateFlow<String>` — 当前筛选类型
- `searchQuery: StateFlow<String>` — 搜索关键词
- `editingRecord: StateFlow<WorkoutRecord?>` — 编辑中的记录
- `defaultType: StateFlow<String>` — 从 DataStore 读取的默认类型
- `exerciseState: StateFlow<ExerciseUiState>` — 网络推荐状态

### StatsViewModel

管理统计数据状态：

```kotlin
sealed interface StatsUiState {
    data object Loading : StatsUiState
    data class Success(...) : StatsUiState
    data class Error(val message: String) : StatsUiState
}
```

- 聚合统计（StatsData）
- 运动推荐列表
- 活跃目标列表
- 最近记录列表

### SettingsViewModel

管理用户偏好（双向绑定 DataStore）。

---

## 九、Repository 设计

`WorkoutRepository` 作为唯一数据入口，隔离本地和网络数据：

```
WorkoutRepository
├── 本地数据 (WorkoutDao + GoalDao)
│   ├── getAllRecords() → Flow<List<WorkoutRecord>>
│   ├── insertRecord() / updateRecord() / deleteRecord()
│   ├── searchRecords() / getByType() / getStatsByType()
│   └── getActiveGoals() / insertGoal() / updateGoal()
└── 网络数据 (RetrofitClient.apiService)
    ├── fetchExercises() → List<ExerciseDto>
    └── searchExercisesOnline() → List<ExerciseDto>
```

- ViewModel 不直接调用 DAO
- 所有数据操作通过 Repository 统一入口

---

## 十、项目结构

```
app/src/main/java/com/example/campusassistant/
├── MainActivity.kt              # 活动入口
├── CampusAssistantApp.kt        # Application（Coil 配置）
├── data/
│   ├── entity/
│   │   ├── WorkoutRecord.kt     # 运动记录 Entity
│   │   └── WorkoutGoal.kt       # 运动目标 Entity
│   ├── dao/
│   │   ├── WorkoutDao.kt        # 运动记录 DAO（含 WorkoutStats 聚合）
│   │   └── GoalDao.kt           # 目标 DAO
│   ├── database/
│   │   └── AppDatabase.kt       # Room 数据库
│   ├── network/
│   │   ├── ApiService.kt        # Retrofit 接口（wger.de）
│   │   ├── RetrofitClient.kt    # OkHttp + Retrofit 单例
│   │   └── dto/
│   │       └── ExerciseDto.kt   # API 返回数据 DTO
│   └── repository/
│       └── WorkoutRepository.kt # 数据仓库
├── datastore/
│   └── UserPreferences.kt       # DataStore 用户偏好
├── navigation/
│   └── AppNavigation.kt         # 导航图 + 底部导航栏
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt        # 记录列表页
│   │   ├── AddEditScreen.kt     # 新增/编辑页
│   │   ├── StatsScreen.kt       # 统计页
│   │   └── SettingsScreen.kt    # 设置页
│   ├── components/
│   │   ├── WorkoutCard.kt       # 运动记录卡片 + 类型图标映射
│   │   ├── StatsGrid.kt         # 统计网格卡片
│   │   └── LoadingErrorView.kt  # 通用加载/错误/空状态
│   └── theme/
│       ├── Color.kt             # 主题配色 + 运动类型色
│       ├── Theme.kt             # Material 3 主题（深浅色）
│       └── Type.kt              # 字体排版
└── viewmodel/
    ├── WorkoutViewModel.kt      # 记录页 ViewModel
    ├── StatsViewModel.kt        # 统计页 ViewModel
    └── SettingsViewModel.kt     # 设置页 ViewModel
```

---

## 十一、应用运行截图

| 页面 | 说明 |
|------|------|
| 📱 记录列表页 | 展示运动记录列表，支持类型筛选和搜索 |
| 📊 统计页 | 运动数据统计、每日目标进度、运动推荐 |
| ✏️ 新增运动页 | 填写运动名称、类型、时长、卡路里等信息 |
| ⚙️ 设置页 | 设置默认运动类型和每日卡路里目标 |

（截图请放置在与 report.md 同级的 screenshots/ 目录下，并在下方插入图片引用）

```markdown
![记录列表页](screenshots/home.png)
![统计页](screenshots/stats.png)
![新增运动](screenshots/add.png)
![设置页](screenshots/settings.png)
```

---

## 十二、遇到的问题与解决过程

### 问题 1：Open Library API 在国内无法访问

**现象**：最初使用 Open Library API 作为网络数据源，但在国内环境下请求超时（15秒无响应），导致搜索页面长时间无反馈。

**原因**：Open Library 服务器在美国，国内直接访问延迟极高。

**解决过程**：
1. 尝试缩短超时时间（15s → 10s → 8s），仍然失败
2. 添加 OkHttp 连接池实现连接复用，改善不大
3. 最终方案：切换为 wger.de 运动 API（德国服务器，国内可访问但有时也不稳定）
4. 为应对网络不稳定，添加了降级策略：网络失败时自动使用本地模拟数据

**经验**：选择 API 时应考虑目标用户所在地区的网络环境；同时做好降级/离线保护。

### 问题 2：Modifier.weight() 编译错误

**现象**：`StatsGrid.kt` 编译报错 `Modifier.weight()` 调用错误，提示不在 RowScope 上下文中。

**原因**：`Modifier.weight()` 是 `RowScope` 的扩展函数。在独立的 `@Composable fun StatCard()` 中调用 `.weight()` 无效，因为该函数不在 RowScope 环境内。

**解决过程**：
1. 将 `.weight(1f)` 从 `StatCard` 内部移到 `Row {}` 中
2. 通过 `StatCard` 的 `modifier` 参数传入，在 Row 作用域内调用

**经验**：Compose 的 Scope 限定修饰符（如 weight、align）只能在对应的布局容器 lambda 内直接调用。

### 问题 3：LazyColumn 底部内容被导航栏遮挡

**现象**：统计页和记录页的列表内容滚动不到最底部，最后几条记录被底部导航栏遮挡。

**原因**：`LazyColumn` 的 `contentPadding` 没有给底部预留足够空间。

**解决过程**：将 `contentPadding` 的 `bottom` 从 12dp 改为 80dp。

### 问题 4：设置页面无法滚动

**现象**：设置页面内容超出屏幕时无法下滑查看。

**原因**：使用了普通 `Column` 而非 `LazyColumn`，且没有添加 `verticalScroll` 修饰符。

**解决过程**：添加 `Modifier.verticalScroll(rememberScrollState())`。

---

## 十三、已实现的选做项

| 类别 | 选做项 | 实现说明 |
|------|--------|----------|
| A 类 | 复杂数据库查询 | 模糊搜索（LIKE + %）、GROUP BY 分组统计、按类型筛选 |
| B 类 | 网络图片加载 | 使用 Coil `AsyncImage` + 磁盘缓存 |
| B 类 | 网络缓存/降级 | 网络失败时自动使用模拟数据展示 |
| C 类 | 搜索体验优化 | 搜索框可折叠、关键词实时筛选 |
| B 类 | 连接池优化 | OkHttp ConnectionPool（5 连接复用，5 分钟保持） |

---

## 十四、AI 使用说明

| 项目 | 内容 |
|------|------|
| **是否使用 AI** | ✅ 是 |
| **使用工具** | CodeBuddy (基于 Deepseek-V4-Pro) |
| **用途说明** | AI 辅助完成了项目整体架构设计、全部代码编写、UI 美化、编译错误修复、功能优化及本报告的撰写。开发过程中通过对话迭代了约 20+ 轮，从最初的书香校园（CampusAssistant）图书管理应用改造为运动记录（FitTrack）应用，包括删除旧代码、重写数据层、UI 层、ViewModel 层，以及后续的多人次交互优化和 bug 修复。 |
| **人工参与** | 运行测试、截图验证、提出需求、确认设计方案 |

---

## 十五、运行说明

### 最低要求

- Android Studio Ladybug (2024.2+) 或更新版本
- JDK 17+
- Android SDK 35
- Gradle 8.11.1

### 运行步骤

1. 用 Android Studio 打开项目根目录 `CampusAssistant/`
2. 等待 Gradle 同步完成
3. 连接 Android 模拟器或真机（API 24+）
4. 点击 Run 按钮运行

### 特殊权限

- `android.permission.INTERNET` — 网络请求（获取运动推荐数据）

---

## 十六、项目亮点

1. **网络降级策略**：API 不可用时自动切换本地数据，用户体验不中断
2. **运动类型可视化**：6 种运动类型各有专属颜色和图标，分类统计一目了然
3. **深浅色双模式**：自适应系统主题，支持 Android 12+ 动态取色
4. **架构规范**：严格遵循 MVVM + Repository 模式，数据流单向，职责清晰
5. **UI 打磨**：圆形图标 + 彩色标签 + 平滑圆角卡片，Material 3 设计规范

## 十七、未来改进方向

1. 增加图表库绘制运动趋势折线图
2. 支持导出运动数据为 CSV/JSON
3. 添加运动提醒通知功能
4. 接入更多国内可用的运动 API
5. 增加下拉刷新功能
6. 添加运动历史日历视图

---

*报告完成日期：2026年6月20日*
