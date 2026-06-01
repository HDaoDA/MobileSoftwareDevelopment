package com.example.bookshelf.data

import com.example.bookshelf.model.Book

interface BooksRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(id: String): Book?
}