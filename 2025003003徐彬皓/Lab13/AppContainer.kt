package com.example.bookshelf

import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.data.NetworkBooksRepository
import com.example.bookshelf.network.BASE_URL
import com.example.bookshelf.network.BookshelfApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val bookshelfApiService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }

    val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(bookshelfApiService)
    }
}