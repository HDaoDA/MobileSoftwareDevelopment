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
    private val offlineBooksRepository: OfflineBooksRepository = OfflineBooksRepository()
) : BooksRepository {

    override suspend fun getBooks(): List<Book> {
        return try {
            bookshelfApiService.getBooks().map { bookDto ->
                bookDto.asExternalModel()
            }
        } catch (e: Exception) {
            offlineBooksRepository.getBooks()
        }
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().firstOrNull { book ->
            book.id == id
        } ?: throw NoSuchElementException("未找到编号为 $id 的图书")
    }
}

class OfflineBooksRepository : BooksRepository {

    private val offlineBooks = listOf(
        Book(
            id = "1",
            title = "Offline Book #1",
            coverUrl = "https://picsum.photos/id/10/800/600"
        ),
        Book(
            id = "2",
            title = "Offline Book #2",
            coverUrl = "https://picsum.photos/id/11/800/600"
        ),
        Book(
            id = "3",
            title = "Offline Book #3",
            coverUrl = "https://picsum.photos/id/12/800/600"
        ),
        Book(
            id = "4",
            title = "Offline Book #4",
            coverUrl = "https://picsum.photos/id/13/800/600"
        ),
        Book(
            id = "5",
            title = "Offline Book #5",
            coverUrl = "https://picsum.photos/id/14/800/600"
        ),
        Book(
            id = "6",
            title = "Offline Book #6",
            coverUrl = "https://picsum.photos/id/15/800/600"
        )
    )

    override suspend fun getBooks(): List<Book> {
        return offlineBooks
    }

    override suspend fun getBook(id: String): Book {
        return offlineBooks.firstOrNull { book ->
            book.id == id
        } ?: throw NoSuchElementException("未找到编号为 $id 的图书")
    }
}