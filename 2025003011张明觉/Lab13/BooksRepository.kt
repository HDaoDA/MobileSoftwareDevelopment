package com.example.bookshelf.data

import com.example.bookshelf.model.Book

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

class NetworkBooksRepository(
    private val bookshelfApiService: com.example.bookshelf.network.BookshelfApiService
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return bookshelfApiService.getBooks().map { it.asExternalModel() }
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().firstOrNull { it.id == id } 
            ?: Book(id, "https://picsum.photos/id/$id/800/600")
    }
}

class OfflineBooksRepository : BooksRepository {
    private val sampleBooks = listOf(
        Book("1", "https://picsum.photos/id/10/800/600", "Book 1"),
        Book("2", "https://picsum.photos/id/11/800/600", "Book 2"),
        Book("3", "https://picsum.photos/id/12/800/600", "Book 3"),
        Book("4", "https://picsum.photos/id/13/800/600", "Book 4"),
        Book("5", "https://picsum.photos/id/14/800/600", "Book 5"),
        Book("6", "https://picsum.photos/id/15/800/600", "Book 6"),
        Book("7", "https://picsum.photos/id/16/800/600", "Book 7"),
        Book("8", "https://picsum.photos/id/17/800/600", "Book 8"),
        Book("9", "https://picsum.photos/id/18/800/600", "Book 9"),
        Book("10", "https://picsum.photos/id/19/800/600", "Book 10"),
        Book("11", "https://picsum.photos/id/20/800/600", "Book 11"),
        Book("12", "https://picsum.photos/id/21/800/600", "Book 12")
    )

    override suspend fun getBooks(): List<Book> {
        return sampleBooks
    }

    override suspend fun getBook(id: String): Book {
        return sampleBooks.firstOrNull { it.id == id } ?: sampleBooks[0]
    }
}