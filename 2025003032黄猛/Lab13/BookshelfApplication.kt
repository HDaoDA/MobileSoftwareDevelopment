package com.example.bookshelf

import android.app.Application

class BookshelfApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = createDefaultAppContainer()
    }

    fun booksRepository(): BooksRepository {
        return container.booksRepository
    }

    fun isContainerReady(): Boolean {
        return ::container.isInitialized
    }

    fun appName(): String {
        return "Bookshelf"
    }
}
