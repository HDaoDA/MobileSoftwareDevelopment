package com.example.bookshelf.repository

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.asExternalModel
import com.example.bookshelf.network.BookshelfApiService

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

class NetworkBooksRepository(
    private val apiService: BookshelfApiService
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return try {
            apiService.getBooks().map { it.asExternalModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBook(id: String): Book {
        val books = getBooks()
        return books.find { it.id == id }
            ?: throw IllegalArgumentException("Book not found with id: $id")
    }
}

// 兜底数据（断网时使用）
class OfflineBooksRepository : BooksRepository {
    private val offlineBooks = listOf(
        Book("1", "https://picsum.photos/id/10/800/600", "Sample Book 1"),
        Book("2", "https://picsum.photos/id/11/800/600", "Sample Book 2"),
        Book("3", "https://picsum.photos/id/12/800/600", "Sample Book 3"),
        Book("4", "https://picsum.photos/id/13/800/600", "Sample Book 4"),
        Book("5", "https://picsum.photos/id/14/800/600", "Sample Book 5")
    )

    override suspend fun getBooks(): List<Book> = offlineBooks

    override suspend fun getBook(id: String): Book {
        return offlineBooks.find { it.id == id }
            ?: throw IllegalArgumentException("Book not found with id: $id")
    }
}