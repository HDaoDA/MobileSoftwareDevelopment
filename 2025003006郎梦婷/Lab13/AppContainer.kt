package com.example.bookshelf.data

import com.example.bookshelf.network.ApiConfig
import com.example.bookshelf.repository.BooksRepository
import com.example.bookshelf.repository.NetworkBooksRepository

class AppContainer {
    val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(ApiConfig.bookshelfApiService)
    }
}