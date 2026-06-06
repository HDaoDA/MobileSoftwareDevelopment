package com.example.bookshelf.data
import com.example.bookshelf.model.Book
import com.example.bookshelf.model.toBook
import com.example.bookshelf.network.BookshelfApi

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book
}

class NetworkBooksRepository : BooksRepository {
    override suspend fun getBooks(): List<Book> = BookshelfApi.retrofitService.getBooks().map { it.toBook() }
    override suspend fun getBook(id: String): Book = getBooks().first { it.id == id }
}

class OfflineBooksRepository : BooksRepository {
    private val fakeList = listOf(
        Book("1","https://picsum.photos/id/10/800/600","离线书籍1"),
        Book("2","https://picsum.photos/id/11/800/600","离线书籍2"),
        Book("3","https://picsum.photos/id/12/800/600","离线书籍3")
    )
    override suspend fun getBooks() = fakeList
    override suspend fun getBook(id: String) = fakeList.first { it.id == id }
}