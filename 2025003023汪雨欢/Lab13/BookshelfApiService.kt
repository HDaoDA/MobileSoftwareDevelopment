package com.example.bookshelf.network

import com.example.bookshelf.model.BookDto
import retrofit2.http.GET

interface BookshelfApiService {
    // 获取书籍图片列表
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}