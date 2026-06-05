package com.example.bookshelf.data
interface AppContainer {
    val booksRepository: BooksRepository
}
class DefaultAppContainer : AppContainer {
    //切换注释即可切换离线/网络数据源
    override val booksRepository: BooksRepository = NetworkBooksRepository()
    //override val booksRepository: BooksRepository = OfflineBooksRepository()
}