package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.network.RetrofitInstance
import com.example.bookshelf.model.toBookList

// 仓库接口
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

// 网络数据源实现
class NetworkBooksRepository : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return RetrofitInstance.apiService.getBooks().toBookList()
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().first { it.id == id }
    }
}

// 离线/断网兜底数据源
class OfflineBooksRepository : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return listOf(
            Book("1", "https://picsum.photos/id/10/800/600"),
            Book("2", "https://picsum.photos/id/11/800/600"),
            Book("3", "https://picsum.photos/id/12/800/600")
        )
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().first { it.id == id }
    }
}