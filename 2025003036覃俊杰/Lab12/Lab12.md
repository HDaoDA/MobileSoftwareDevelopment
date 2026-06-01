# Lab12：Kotlin 协程入门练习

## 实验背景

本次实验介绍 **Kotlin 协程（Coroutines）**，这是 Android 开发中处理异步任务的核心工具。协程可以在不阻塞线程的情况下挂起和恢复执行，让你用同步的写法写出异步的代码。

建议使用 [Kotlin Playground](https://play.kotlinlang.org/) 在线编写并运行代码，完成后将代码粘贴到对应代码块中提交。

**codelab-coroutines-annotated.md文件仅做参考,不要求上交**

---

## 前提条件

- 能够使用 `main()` 函数创建基本的 Kotlin 程序
- 了解 Kotlin 语言的基础知识，包括函数和 lambda
- 熟悉 Kotlin Playground 的使用

---

## 提交要求

```
学号姓名/
└── Lab12/
    └── Lab12.md    # 将答案填入本文件各题代码块后提交
```

截止时间：**2026-05-29**，届时关于 Lab12 的 PR 请求将不会被合并。

---

所有题目均需导入以下包：

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
```

---

## 时间戳打印方法

本次实验所有题目的预期输出都带有时间戳（如 `[0.0s]`、`[1.2s]`），用于观察代码执行顺序和耗时。你需要：

1. 在文件顶层定义一个起始时间变量：
   ```kotlin
   val startTime = System.currentTimeMillis()
   ```
2. 每次 `println` 时，计算从程序启动到当前的秒数差：
   ```kotlin
   println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 你的消息")
   ```

`System.currentTimeMillis()` 返回的是毫秒值，除以 `1000.0` 得到秒数（保留一位小数）。

---

## 题目 1：餐厅后厨模拟（suspend 函数 & runBlocking）

### 背景

一家餐厅的后厨需要准备三道菜，每道菜的烹饪时间不同。用 `delay()` 模拟烹饪耗时——它是协程库提供的**挂起函数**，只能在协程或其他挂起函数中调用。`runBlocking()` 用于在普通 `main()` 函数中创建协程运行环境。

### 你需要做的

1. 创建三个挂起函数，料理时间分别为 800ms、1500ms、500ms：
   - `cookSoup()` — 输出 `"[时间] 汤已煮好"`
   - `cookSteak()` — 输出 `"[时间] 牛排煎好"`
   - `cookSalad()` — 输出 `"[时间] 沙拉已备好"`
2. 在 `main()` 中用 `runBlocking` 依次调用这三个函数
3. 用 `measureTimeMillis` 测量总耗时并输出

### 预期输出

```
[0.0s] 后厨开始工作
[0.8s] 汤已煮好
[2.3s] 牛排煎好
[2.8s] 沙拉已备好
[2.8s] 全部完成，总耗时约 2.8 秒
```

> 三道菜顺序完成，总耗时 ≈ 各道菜时间之和。

### 你的答案

```
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 程序开始时间
private val startTime = System.currentTimeMillis()

// 统一时间戳输出函数
fun log(message: String) {
    val time = (System.currentTimeMillis() - startTime) / 1000.0
    println("[${"%.1f".format(time)}s] $message")
}

// 挂起函数：制作汤
suspend fun cookSoup() {
    delay(800)
    println("汤已煮好")
}

// 挂起函数：制作牛排
suspend fun cookSteak() {
    delay(1500)
    println("牛排煎好")
}

// 挂起函数：制作沙拉
suspend fun cookSalad() {
    delay(500)
    println("沙拉已备好")
}

fun main() = runBlocking {

    log("后厨开始工作")

    val totalTime = measureTimeMillis {

        // 顺序执行三道菜
        cookSoup()
        cookSteak()
        cookSalad()
    }

    println("全部完成，总耗时约 ${"%.1f".format(totalTime / 1000.0)} 秒")
}
```



---

## 题目 2：快递站并发取件（launch 并发）

### 背景

快递站有三个窗口分别处理不同类型的包裹。为了效率，三个窗口应**同时**工作。

`launch()` 是"触发后不理"：启动协程后立即返回，不等待其完成。但 `runBlocking` 会等作用域内**所有**协程完成后才退出。

> **关键原则**：结构化并发要求**显式声明**并发——默认代码依序执行，除非你主动使用 `launch()` 或 `async()`。

### 你需要做的

1. 创建三个挂起函数，模拟各窗口处理包裹的时间：
   - `windowA()` — delay 1200ms，输出 `"A窗口：包裹已出库"`
   - `windowB()` — delay 900ms，输出 `"B窗口：包裹已出库"`
   - `windowC()` — delay 1500ms，输出 `"C窗口：包裹已出库"`
2. 用 `launch {}` 启动三个协程分别调用（不要顺序调用）
3. 在启动所有协程后**立即**输出 `"所有窗口已开放，请等待取件..."`
4. 用 `measureTimeMillis` 验证总耗时约为最慢窗口的时间（~1.5 秒）而非三个窗口之和

### 预期输出

```
[0.0s] 快递站开门
[0.0s] 所有窗口已开放，请等待取件...
[0.9s] B窗口：包裹已出库
[1.2s] A窗口：包裹已出库
[1.5s] C窗口：包裹已出库
[1.5s] 全部完成，总耗时约 1.5 秒
```

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
val startTime = System.currentTimeMillis()

suspend fun windowA() {
    delay(1200)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] A窗口：包裹已出库")
}

suspend fun windowB() {
    delay(900)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] B窗口：包裹已出库")
}

suspend fun windowC() {
    delay(1500)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] C窗口：包裹已出库")
}

fun main() = runBlocking {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 快递站开门")
    val totalTime = measureTimeMillis {
        // launch 并发启动
        launch { windowA() }
        launch { windowB() }
        launch { windowC() }
        // 立即输出
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 所有窗口已开放，请等待取件...")
    }
    val second = totalTime / 1000.0
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 全部完成，总耗时约 $second 秒")
}

```

---

## 题目 3：用户主页数据聚合（async & await）

### 背景

某社交 App 的用户主页需要同时展示**昵称、未读消息数、最新动态**。这三部分数据互不依赖，可以并发获取。与 `launch()` 不同，这里需要拿回返回值，因此使用 `async()`。

- `async {}` 返回 `Deferred<T>`，代表"未来的结果"
- `await()` 获取结果——若还没就绪则挂起等待

### 你需要做的

1. 创建三个返回值的挂起函数：
   - `fetchNickname()` — delay 600ms，返回 `"User_888"`
   - `fetchUnreadCount()` — delay 400ms，返回 `5`
   - `fetchLatestFeed()` — delay 1000ms，返回 `"今天学习了 Kotlin 协程"`
2. 用 `async {}` 并发调用，拿到各自的 `Deferred` 对象
3. 用 `await()` 获取结果，按格式输出用户主页
4. 用 `measureTimeMillis` 验证总耗时约等于最慢请求的时间（~1.0 秒）

### 预期输出

```
[0.0s] 开始加载用户主页...
[1.0s] ========== 用户主页 ==========
[1.0s] 昵称：User_888
[1.0s] 未读消息：5 条
[1.0s] 最新动态：今天学习了 Kotlin 协程
[1.0s] 加载完成，总耗时约 1.0 秒
```

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 程序开始时间
private val startTime = System.currentTimeMillis()

// 统一日志输出函数
fun log(message: String) {
    val time = (System.currentTimeMillis() - startTime) / 1000.0
    println("[${"%.1f".format(time)}s] $message")
}

// 获取昵称
suspend fun fetchNickname(): String {
    delay(600)
    return "User_888"
}

// 获取未读消息数量
suspend fun fetchUnreadCount(): Int {
    delay(400)
    return 5
}

// 获取最新动态
suspend fun fetchLatestFeed(): String {
    delay(1000)
    return "今天学习了 Kotlin 协程"
}

fun main() = runBlocking {

    println("开始加载用户主页...")

    val totalTime = measureTimeMillis {

        // async 并发请求数据
        val nicknameDeferred = async { fetchNickname() }
        val unreadDeferred = async { fetchUnreadCount() }
        val feedDeferred = async { fetchLatestFeed() }

        // 等待所有结果返回
        val nickname = nicknameDeferred.await()
        val unreadCount = unreadDeferred.await()
        val latestFeed = feedDeferred.await()

        // 输出主页信息
       	println("========== 用户主页 ==========")
        println("昵称：$nickname")
        println("未读消息：$unreadCount 条")
        println("最新动态：$latestFeed")
    }

    println("加载完成，总耗时约 ${"%.1f".format(totalTime / 1000.0)} 秒")
}
```

---

## 题目 4：旅行规划助手（coroutineScope 并行分解）

### 背景

用户输入目的地后，系统需同时查询**机票、酒店、当地天气**，然后合并成旅行计划。这就是**并行分解**：把大任务拆成可并发的子任务，全部完成后合并结果。

`coroutineScope {}` 创建一个局部作用域——内部协程全部完成后它才返回。调用方看到的只是一个普通的挂起函数调用，**并发是实现细节**。

> `coroutineScope` vs `runBlocking`：前者是挂起函数（不阻塞线程），后者是普通函数（阻塞线程）。Android 中应使用 `coroutineScope`。

### 你需要做的

1. 创建三个挂起函数：
   - `searchFlights(destination: String)` — delay 1200ms，返回 `"北京 → $destination，¥1280"`
   - `searchHotels(destination: String)` — delay 800ms，返回 `"$destination 中心酒店，¥450/晚"`
   - `checkWeather(destination: String)` — delay 500ms，返回 `"$destination 明日晴，22°C"`
2. 创建 `planTrip(destination: String) = coroutineScope { ... }`，内部用 `async` 并发调用三者，返回拼接好的旅行计划
3. `main()` 中调用 `planTrip("上海")` 并输出结果

### 预期输出

```
[0.0s] 正在为你规划上海之旅...
[0.5s] 天气已查询
[0.8s] 酒店已查询
[1.2s] 机票已查询
[1.2s] ========== 旅行计划 ==========
[1.2s] 机票：北京 → 上海，¥1280
[1.2s] 酒店：上海 中心酒店，¥450/晚
[1.2s] 天气：上海 明日晴，22°C
```

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
val startTime = System.currentTimeMillis()

suspend fun searchFlights(destination: String): String {
    delay(1200)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 机票已查询")
    return "北京 → $destination，¥1280"
}

suspend fun searchHotels(destination: String): String {
    delay(800)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 酒店已查询")
    return "$destination 中心酒店，¥450/晚"
}

suspend fun checkWeather(destination: String): String {
    delay(500)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 天气已查询")
    return "$destination 明日晴，22°C"
}

suspend fun planTrip(destination: String) = coroutineScope {
    val flight = async { searchFlights(destination) }
    val hotel = async { searchHotels(destination) }
    val weather = async { checkWeather(destination) }

    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ========== 旅行计划 ==========")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 机票：${flight.await()}")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 酒店：${hotel.await()}")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 天气：${weather.await()}")
}

fun main() = runBlocking {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 正在为你规划上海之旅...")
    planTrip("上海")
}

```

---

## 题目 5：支付流程异常处理（协程异常）

### 背景

某支付流程需要同时**验证余额**和**检查风控**。风控系统偶尔故障，如果直接让异常传播，按照协程规则——**子协程异常 → 父协程取消 → 兄弟协程全部取消**——连余额验证的结果也会丢失。

需要选择正确的异常处理位置：

| 策略 | 做法 | 效果 |
|------|------|------|
| 外层捕获 | try-catch 包裹 `await()` | 所有结果都丢失，程序不崩溃 |
| 内层捕获 | try-catch 放在 `async {}` 内部 | 部分成功，失败的返回 fallback 值 |

> **警告**：避免 `catch(e: Exception)`，应只捕获你能处理的特定异常类型，否则会误吞 `CancellationException`。

### 你需要做的

1. 创建两个挂起函数：
   - `checkBalance()` — delay 500ms，返回 `"余额充足"`
   - `checkRiskControl()` — delay 300ms，抛出 `RuntimeException("风控系统异常")`
2. 用 `async {}` 并发调用它们
3. **在 `checkRiskControl` 的 `async {}` 内部** try-catch 异常，返回 fallback 字符串 `"风控未通过（已降级处理）"`
4. `checkBalance` 正常返回，最终拼接输出支付结果

### 预期输出

```
[0.0s] 开始支付验证...
[0.3s] 风控检查异常：风控系统异常，已降级处理
[0.5s] 余额验证通过
[0.5s] ========== 支付结果 ==========
[0.5s] 余额：余额充足
[0.5s] 风控：风控未通过（已降级处理）
[0.5s] 支付已提交（风控降级模式）
```

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
val startTime = System.currentTimeMillis()

suspend fun checkBalance(): String {
    delay(500)
    return "余额充足"
}

suspend fun checkRiskControl(): String {
    delay(300)
    throw RuntimeException("风控系统异常")
}

fun main() = runBlocking {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 开始支付验证...")
    
    val balance = async { checkBalance() }
    val risk = async {
        try {
            checkRiskControl()
        } catch (e: RuntimeException) {
            println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 风控检查异常：${e.message}，已降级处理")
            "风控未通过（已降级处理）"
        }
    }

    val balanceRes = balance.await()
    val riskRes = risk.await()
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 余额验证通过")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ========== 支付结果 ==========")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 余额：$balanceRes")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 风控：$riskRes")
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 支付已提交（风控降级模式）")
}

```

---

## 题目 6：搜索防抖（协程取消）

### 背景

用户在搜索框快速输入时，不希望每次按键都发起一次完整的搜索请求。常见做法是：等用户停止输入一段时间后再发起搜索。如果用户继续输入，则**取消**上一个还未完成的搜索，只保留最新的。

协程取消是**协作式**的——`cancel()` 发出信号后，挂起函数（如 `delay()`）会自动检测并终止。取消**向下传播**（父 → 子），不影响兄弟协程。

### 你需要做的

1. 创建挂起函数 `performSearch(query: String)`：
   - delay 2000ms 模拟耗时搜索
   - 输出 `"[时间] 关于「$query」的搜索结果：42 条"`
2. 模拟两次输入：
   - 启动第一个搜索协程 `searchJob1`，query = `"Kotlin"`
   - delay 500ms 后取消 `searchJob1`，输出 `"搜索「Kotlin」已取消（用户继续输入）"`
   - 取消后立即启动第二个搜索协程，query = `"Kotlin 协程"`
   - 等待第二个搜索完成

### 预期输出

```
[0.0s] 开始搜索：Kotlin
[0.5s] 搜索「Kotlin」已取消（用户继续输入）
[0.5s] 开始搜索：Kotlin 协程
[2.5s] 关于「Kotlin 协程」的搜索结果：42 条
```

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
val startTime = System.currentTimeMillis()

suspend fun performSearch(query: String) {
    delay(2000)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 关于「$query」的搜索结果：42 条")
}

fun main() = runBlocking {
    // 第一次搜索
    val searchJob1 = launch {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 开始搜索：Kotlin")
        performSearch("Kotlin")
    }

    // 500ms后取消
    delay(500)
    searchJob1.cancel()
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 搜索「Kotlin」已取消（用户继续输入）")

    // 第二次搜索
    val searchJob2 = launch {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] 开始搜索：Kotlin 协程")
        performSearch("Kotlin 协程")
    }
    searchJob2.join() // 等待完成
}

```

---

## 题目 7：图片处理服务（Dispatcher & withContext）

### 背景

Android 黄金法则：**主线程神圣不可阻塞**。协程通过**调度程序（Dispatcher）**决定代码在哪个线程上执行：

| 调度程序 | 用途 |
|----------|------|
| `Dispatchers.Main` | UI 更新等快速工作（Kotlin Playground 不支持） |
| `Dispatchers.IO` | 磁盘读写、网络请求等 I/O 操作 |
| `Dispatchers.Default` | CPU 密集型计算，如图片处理、排序 |

`withContext(dispatcher)` 临时切换调度器：挂起 → 在目标线程执行 → 回到原线程。

### 你需要做的

1. 创建三个挂起函数，每个内部打印当前线程名（用 `Thread.currentThread().name`）：
   - `loadImage()` — delay 500ms，输出 `"图片已加载（IO 线程）"`
   - `processImage()` — delay 1000ms，输出 `"图片处理完成（Default 线程）"`
   - `saveImage()` — delay 300ms，输出 `"图片已保存（回到主线程）"`
2. 在 `launch` 块中依次调用这三个函数
3. `loadImage()` 内部用 `withContext(Dispatchers.IO)` 包裹
4. `processImage()` 内部用 `withContext(Dispatchers.Default)` 包裹
5. `saveImage()` 不切换（让它回到原线程执行）
6. 观察输出中的线程变化

### 预期输出（参考，线程名以实际运行为准）

```
[0.0s] main @coroutine#1 - 开始图片处理
[0.0s] main @coroutine#2 - launch 启动
[0.5s] DefaultDispatcher-worker-1 @coroutine#2 - 图片已加载（IO 线程）
[1.5s] DefaultDispatcher-worker-1 @coroutine#2 - 图片处理完成（Default 线程）
[1.8s] main @coroutine#2 - 图片已保存（回到主线程）
[1.8s] main @coroutine#2 - 全部处理完成
```

> 关键点：同一个 `@coroutine#2` 在不同阶段运行在不同线程上。

### 你的答案

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
val startTime = System.currentTimeMillis()

suspend fun loadImage() {
    withContext(Dispatchers.IO) {
        delay(500)
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - 图片已加载（IO 线程）")
    }
}

suspend fun processImage() {
    withContext(Dispatchers.Default) {
        delay(1000)
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - 图片处理完成（Default 线程）")
    }
}

suspend fun saveImage() {
    delay(300)
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - 图片已保存（回到主线程）")
}

fun main() = runBlocking {
    println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - 开始图片处理")
    val job = launch {
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - launch 启动")
        loadImage()
        processImage()
        saveImage()
        println("[${(System.currentTimeMillis() - startTime) / 1000.0}s] ${Thread.currentThread().name} - 全部处理完成")
    }
    job.join()
}

```

