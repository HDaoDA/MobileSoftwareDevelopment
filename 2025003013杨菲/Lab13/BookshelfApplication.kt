package com.example.bookshelf

import android.app.Application
import com.example.bookshelf.data.AppContainer
import com.example.bookshelf.data.RealAppContainer

class BookshelfApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // 使用真实网络数据
        container = RealAppContainer()

        // 如果想测试离线模式，替换为：
        // container = OfflineAppContainer()
    }
}