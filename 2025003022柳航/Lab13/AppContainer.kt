package com.example.bookshelf

import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.data.NetworkBooksRepository
import com.example.bookshelf.network.BookshelfApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: BookshelfApiService = retrofit.create(BookshelfApiService::class.java)

    val booksRepository: BooksRepository = NetworkBooksRepository(api)
}