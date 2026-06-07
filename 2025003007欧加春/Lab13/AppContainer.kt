package com.example.bookshelf.data

import android.content.Context
import com.example.bookshelf.network.BASE_URL
import com.example.bookshelf.network.BookshelfApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 应用级容器，集中创建 Retrofit、API Service 和 Repository
 * 实现手动依赖注入
 */
interface AppContainer {
    val booksRepository: BooksRepository
}

/**
 * 默认实现：使用 Retrofit 从网络获取数据
 */
class DefaultAppContainer(context: Context) : AppContainer {

    private val baseUrl = BASE_URL

    private val gson: Gson by lazy {
        GsonBuilder()
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val retrofitService: BookshelfApiService by lazy {
        retrofit.create(BookshelfApiService::class.java)
    }

    override val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(retrofitService)
    }
}
