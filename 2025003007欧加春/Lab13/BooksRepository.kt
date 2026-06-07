package com.example.bookshelf.data

import com.example.bookshelf.model.Book

/**
 * Repository 接口，隔离数据源
 * ViewModel 只依赖此接口，不直接依赖 Retrofit
 */
interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}
