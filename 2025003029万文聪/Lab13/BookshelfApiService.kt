package com.example.bookshelf.network

import com.example.bookshelf.model.BookDto
import retrofit2.http.GET

interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
