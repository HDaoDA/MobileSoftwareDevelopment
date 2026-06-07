package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.asExternalModel
import com.example.bookshelf.network.BookshelfApiService

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

class NetworkBooksRepository(
    private val bookshelfApiService: BookshelfApiService,
    private val offlineBooksRepository: BooksRepository,
) : BooksRepository {
    private var cachedBooks: List<Book> = emptyList()

    override suspend fun getBooks(): List<Book> {
        return try {
            bookshelfApiService.getBooks()
                .map { it.asExternalModel() }
                .also { cachedBooks = it }
        } catch (e: Exception) {
            // 网络异常时使用兜底数据，保证断网时应用仍能展示界面。
            offlineBooksRepository.getBooks()
        }
    }

    override suspend fun getBook(id: String): Book {
        val books = if (cachedBooks.isEmpty()) getBooks() else cachedBooks
        return books.firstOrNull { it.id == id } ?: offlineBooksRepository.getBook(id)
    }
}

class OfflineBooksRepository : BooksRepository {
    private val offlineBooks = listOf(
        Book("1", "Offline Book #1", "https://picsum.photos/id/30/800/600"),
        Book("2", "Offline Book #2", "https://picsum.photos/id/31/800/600"),
        Book("3", "Offline Book #3", "https://picsum.photos/id/32/800/600"),
        Book("4", "Offline Book #4", "https://picsum.photos/id/33/800/600"),
        Book("5", "Offline Book #5", "https://picsum.photos/id/34/800/600"),
        Book("6", "Offline Book #6", "https://picsum.photos/id/35/800/600"),
    )

    override suspend fun getBooks(): List<Book> = offlineBooks

    override suspend fun getBook(id: String): Book {
        return offlineBooks.firstOrNull { it.id == id } ?: offlineBooks.first()
    }
}
