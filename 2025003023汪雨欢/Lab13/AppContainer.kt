package com.example.bookshelf.data

// 全局容器：统一提供 Repository 实例
object AppContainer {
    // 切换网络/离线数据源
    val booksRepository: BooksRepository = NetworkBooksRepository()
    // 断网测试时换成下面这句
    // val booksRepository: BooksRepository = OfflineBooksRepository()
}