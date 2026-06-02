package com.example.bookshelf

import android.app.Application

class BookshelfApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}