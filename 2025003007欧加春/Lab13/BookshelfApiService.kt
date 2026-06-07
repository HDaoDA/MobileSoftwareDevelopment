package com.example.bookshelf.network

import com.example.bookshelf.model.BookDto
import retrofit2.http.GET

/**
 * Retrofit 服务接口，用于请求 Apifox Mock 图片数据
 */
interface BookshelfApiService {
    @GET("photos")
    suspend fun getBooks(): List<BookDto>
}
