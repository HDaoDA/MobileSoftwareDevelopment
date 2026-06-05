package com.example.bookshelf

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

class NetworkBooksRepository(
    private val api: BookshelfApiService,
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return api.getBooks().map { it.asExternalModel() }
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().first { it.id == id }
    }
}

class OfflineBooksRepository : BooksRepository {
    private val cachedBooks = listOf(
        Book(id = "1", coverUrl = "https://picsum.photos/id/10/800/600", title = "Book #1"),
        Book(id = "2", coverUrl = "https://picsum.photos/id/11/800/600", title = "Book #2"),
        Book(id = "3", coverUrl = "https://picsum.photos/id/12/800/600", title = "Book #3"),
        Book(id = "4", coverUrl = "https://picsum.photos/id/13/800/600", title = "Book #4"),
    )

    override suspend fun getBooks(): List<Book> {
        return cachedBooks
    }

    override suspend fun getBook(id: String): Book {
        return cachedBooks.firstOrNull { it.id == id } ?: cachedBooks.first()
    }
}

class FallbackBooksRepository(
    private val networkRepository: BooksRepository,
    private val offlineRepository: BooksRepository,
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        return runCatching { networkRepository.getBooks() }
            .getOrElse { offlineRepository.getBooks() }
    }

    override suspend fun getBook(id: String): Book {
        return runCatching { networkRepository.getBook(id) }
            .getOrElse { offlineRepository.getBook(id) }
    }
}
