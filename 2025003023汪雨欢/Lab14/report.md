# Lab14 实验报告：使用 Room 完成 Bus Schedule 公交时刻表应用
## 一、实验目的
掌握 Android Room 本地数据库的使用，完成实体类映射、DAO 数据访问、数据库初始化，结合 ViewModel、Kotlin Flow 与 Jetpack Compose，读取 `assets` 预置 SQLite 数据库数据并展示在界面中。

## 二、核心组件职责说明
1. **Entity（BusSchedule）**
作为 Room 实体类，作用是将 Kotlin 数据类与 SQLite 数据表 `Schedule` 进行映射，通过注解声明表名、主键、字段映射关系，让 Room 能够识别数据表结构。

2. **DAO（BusScheduleDao）**
数据访问接口，使用 `@Dao` 注解标记，通过 `@Query` 编写原生 SQL 查询语句，定义数据读取方法，是应用访问数据库的唯一入口。

3. **RoomDatabase（BusScheduleDatabase）**
Room 数据库主类，继承自 `RoomDatabase`，使用单例模式保证全局只有一个数据库实例；同时配置数据库版本、关联实体类，并向外提供 DAO 对象，负责数据库整体初始化与管理。

## 三、数据类与数据表映射关系
预置数据库表名为 `Schedule`，`BusSchedule` 实体类通过注解完成字段映射：

| Kotlin 属性 | 数据库列名 | 注解说明 |
|------------|-----------|----------|
| id | id | `@PrimaryKey`，数据表主键 |
| stopName | stop_name | `@ColumnInfo(name = "stop_name")`，映射站点名称字段 |
| arrivalTimeInMillis | arrival_time | `@ColumnInfo(name = "arrival_time")`，映射时间戳字段 |

映射严格匹配数据库原始表结构，保证 Room 可以正常解析数据表。

## 四、DAO 查询语句说明
本项目定义两条查询方法，返回 `Flow<List<BusSchedule>>` 实现数据响应式监听：
1. `getAll()`
SQL：`SELECT * FROM Schedule ORDER BY arrival_time ASC`
功能：查询数据表中**所有**公交时刻表数据，并按照到站时间 `arrival_time` **升序**排列，用于首页展示完整列表。

2. `getByStopName(stopName: String)`
SQL：`SELECT * FROM Schedule WHERE stop_name = :stopName ORDER BY arrival_time ASC`
功能：根据传入的站点名称做条件筛选，查询该站点下所有到站记录，同样按时间升序排列，用于站点详情页。

**排序原因**：公交时刻表需要按照到站先后顺序展示，升序排序符合实际使用逻辑，保证数据展示有序。

## 五、createFromAsset 方法作用
`createFromAsset("database/bus_schedule.db")` 是 Room 提供的预置数据库加载方法：
1. 从项目 `src/main/assets/database/` 目录下读取现成的 `bus_schedule.db` SQLite 文件；
2. 应用首次安装启动时，自动将该数据库文件复制到应用私有目录，完成数据库初始化；
3. 无需在代码中手动编写数据插入逻辑，直接使用预置的 31 条测试数据，简化开发流程。

## 六、ViewModel 数据源切换逻辑
1. **改造前**：`BusScheduleViewModel` 使用 `flowOf()` 返回硬编码的示例数据，界面仅展示单条测试数据；
2. **改造后**：
   - ViewModel 构造函数接收 `BusScheduleDao` 对象，解耦数据层与视图层；
   - 移除固定示例数据，直接调用 DAO 中的查询方法，返回数据库查询得到的 `Flow` 数据流；
   - 通过 ViewModel 工厂 `ViewModelProvider.Factory`，获取全局 Application 上下文，初始化 Room 数据库并实例化 DAO，完成数据源切换。

## 七、Flow 数据流与 Compose 交互流程
1. DAO 方法返回 `Flow<List<BusSchedule>>`，Flow 具备**可观察、可响应**特性，数据变化时自动推送更新；
2. ViewModel 转发 Flow 数据流给 Compose 界面；
3. Compose 中通过 `collectAsState()` 收集 Flow 数据，将其转换为 Compose 可识别的状态；
4. 列表组件读取状态值渲染 UI，当数据库数据发生变化时，界面会**自动重组刷新**，实现响应式 UI。

## 八、实验问题与解决过程
1. **图标资源链接报错**
问题：`mipmap-anydpi-v26` 目录下图标 XML 引用了不存在的 `drawable` 资源，编译失败。
解决：直接删除 `mipmap-anydpi-v26` 文件夹，使用项目原有基础图标，编译恢复正常。

2. **Room 类标红、无法识别**
问题：代码中 `@Entity`、`RoomDatabase` 等注解和类报红。
解决：在项目根目录与 `app` 模块 `build.gradle.kts` 中，添加 Room 运行库、KTX 扩展库、KSP 编译器依赖，同步 Gradle 后问题解决。

3. **应用启动闪退、白屏卡死**
问题：数据库路径书写错误、旧应用数据残留导致版本冲突。
解决：
   - 核对 `createFromAsset` 文件路径，保证与 `assets` 目录结构完全一致；
   - 卸载模拟器中旧版本应用，清除残留数据库文件，重新编译运行。

4. **Compose Divider 废弃警告**
问题：新版本 Material3 中 `Divider` 方法被废弃。
解决：将代码中 `Divider()` 替换为新标准 `HorizontalDivider()`，并补充对应导入。

## 九、实验总结
本次实验完成了 Room 完整数据层搭建，熟练掌握了 **Entity 实体映射、DAO 数据查询、Room 数据库单例初始化** 三大核心模块。同时理解了 `assets` 预置数据库的加载方式、Flow 响应式数据流、ViewModel 数据中转以及 Compose 状态收集的完整调用链路。

最终应用成功读取预置数据库的 31 条公交数据，首页展示完整时刻表，可正常进入站点详情页查看对应记录，所有功能均达到实验要求。