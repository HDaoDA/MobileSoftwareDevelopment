package com.example.bookshelf.data

import com.example.bookshelf.network.ApiConfig

interface AppContainer {
    val booksRepository: BooksRepository
}

// 真实网络数据容器
class RealAppContainer : AppContainer {
    private val apiService = ApiConfig.bookshelfApiService

    override val booksRepository: BooksRepository = NetworkBooksRepository(apiService)
}

// 离线测试容器（断网时使用）
class OfflineAppContainer : AppContainer {
    override val booksRepository: BooksRepository = OfflineBooksRepository()
}