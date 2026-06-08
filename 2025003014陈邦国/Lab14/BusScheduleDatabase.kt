package com.example.busschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room 数据库主类，单例模式
 */
@Database(entities = [BusSchedule::class], version = 1, exportSchema = false)
abstract class BusScheduleDatabase : RoomDatabase() {

    // 暴露DAO对象
    abstract fun busScheduleDao(): BusScheduleDao

    companion object {
        // 单例实例，volatile保证多线程可见性
        @Volatile
        private var INSTANCE: BusScheduleDatabase? = null

        fun getDatabase(context: Context): BusScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BusScheduleDatabase::class.java,
                    "bus_schedule_database"
                )
                // 从assets加载预置数据库
                .createFromAsset("database/bus_schedule.db")
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}