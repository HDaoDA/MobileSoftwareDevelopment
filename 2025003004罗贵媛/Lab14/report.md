\# Lab14 使用Room完成Bus Schedule应用 实验报告

\## 1. Entity、DAO、Database 三者职责说明

1\. \*\*Entity（BusSchedule）\*\*：Room 实体类，作用是\*\*映射 SQLite 数据表\*\*。将 Kotlin 数据类与数据库中 `Schedule` 表做绑定，定义表结构、字段名、主键，是ORM映射的核心。

2\. \*\*DAO（BusScheduleDao）\*\*：数据访问接口，专门定义\*\*数据库增删改查逻辑\*\*。通过 `@Query` 编写SQL语句，提供数据查询方法，是应用操作数据库的唯一入口。

3\. \*\*Database（BusScheduleDatabase）\*\*：Room 数据库主类，负责\*\*数据库整体管理\*\*。配置数据库版本、关联实体类、提供DAO实例，使用单例模式保证全局唯一数据库连接，同时完成预置数据库的加载。



\## 2. BusSchedule 属性与数据表映射关系

数据库表名：`Schedule`

| Kotlin 属性名        | 数据库列名     | 映射说明                     |

|----------------------|---------------|------------------------------|

| id                   | id            | 数据表主键，非空整型         |

| stopName             | stop\_name     | 站点名称，文本类型，非空     |

| arrivalTimeInMillis  | arrival\_time  | 到站时间戳，整型，非空       |

通过 `@Entity` 指定表名，`@ColumnInfo` 手动指定字段映射关系，保证与预置数据库结构完全一致。



\## 3. DAO 查询语句说明与排序原因

\### （1）查询语句作用

1\. `SELECT \* FROM Schedule ORDER BY arrival\_time ASC`：查询数据表中\*\*所有班次数据\*\*，用于首页展示完整公交时刻表。

2\. `SELECT \* FROM Schedule WHERE stop\_name = :stopName ORDER BY arrival\_time ASC`：根据传入的站点名称，\*\*过滤出该站点的所有班次\*\*，用于站点详情页展示。



\### （2）排序原因

`arrival\_time` 是Unix时间戳，代表到站先后顺序。使用 `ORDER BY arrival\_time ASC` 升序排序，可以让页面数据\*\*按照实际到站时间从早到晚展示\*\*，符合公交时刻表的业务逻辑。



\## 4. createFromAsset("database/bus\_schedule.db") 作用

该方法是Room提供的API，作用为：\*\*读取项目 `assets` 目录下的预置SQLite数据库文件 `bus\_schedule.db`，并以此文件作为Room数据库的初始数据\*\*。

实验中项目已提前内置完整业务数据，无需代码手动插入数据，直接复用现成数据库，避免重复造数据。



\## 5. ViewModel 数据切换逻辑

1\. 原始代码：ViewModel 使用 `flowOf()` 硬编码模拟假数据，仅返回单条测试数据。

2\. 改造后逻辑：

&#x20;  - ViewModel 构造函数接收 `BusScheduleDao` 对象；

&#x20;  - 移除所有写死的示例数据，方法直接调用DAO的查询方法；

&#x20;  - 通过 ViewModel 工厂类获取全局Application上下文，初始化Room数据库，再拿到DAO实例注入ViewModel；

&#x20;  - 最终ViewModel向外暴露数据库查询得到的真实数据流。



\## 6. Flow 数据在 Compose 页面的展示流程

1\. ViewModel 的查询方法返回 `Flow<List<BusSchedule>>` 数据流，Flow 具备\*\*数据自动监听\*\*能力，数据变化自动回调。

2\. Compose 页面中使用 `collectAsState(initial = emptyList())` 收集Flow数据流，将流数据转为Compose可识别的状态。

3\. 状态数据绑定到 `LazyColumn` 列表组件，遍历渲染每一条公交班次，完成界面展示。

4\. Flow 结合Room可实现数据库数据实时更新，页面自动刷新，无需手动刷新UI。



\## 7. 实验问题与解决过程

1\. \*\*问题1：表名/列名不匹配，查询无数据\*\*

&#x20;  解决：严格对照预置数据库表结构，`@Entity` 指定表名为 `Schedule`，通过 `@ColumnInfo` 匹配下划线命名的数据库字段，与Kotlin驼峰属性做映射。

2\. \*\*问题2：Room编译报错、注解无法识别\*\*

&#x20;  解决：检查 `app/build.gradle.kts` 中是否完整添加 `room-runtime`、`room-ktx`、`ksp` 编译器依赖，同步Gradle后重新编译。

3\. \*\*问题3：应用启动后仍显示测试假数据\*\*

&#x20;  解决：删除ViewModel中 `flowOf()` 模拟数据，确保方法直接调用DAO查询数据库，同时检查ViewModel工厂是否正常初始化数据库。

4\. \*\*问题4：预置数据库加载失败\*\*

&#x20;  解决：确认 `assets/database/bus\_schedule.db` 路径无误，`createFromAsset` 传入路径与文件实际路径一致。



\## 8. 运行结果说明

1\. 首页：成功加载数据库内31条班次数据，按到站时间升序展示所有公交站点与时间戳格式化后的时间。

2\. 详情页：点击任意站点，页面仅展示该站点所有班次，时间排序正常；顶部返回按钮可正常回到首页。

3\. 横竖屏切换、应用重启后，数据均正常加载，无崩溃、无数据丢失。

