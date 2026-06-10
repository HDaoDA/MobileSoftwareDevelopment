package com.example.busschedule.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busschedule.data.BusSchedule
import com.example.busschedule.data.BusScheduleDao
import com.example.busschedule.data.BusScheduleDatabase
import kotlinx.coroutines.flow.Flow

class BusScheduleViewModel(
    application: Application,
    private val busScheduleDao: BusScheduleDao
) : AndroidViewModel(application) {

    fun getFullSchedule(): Flow<List<BusSchedule>> = busScheduleDao.getAll()

    fun getScheduleFor(stopName: String): Flow<List<BusSchedule>> =
        busScheduleDao.getByStopName(stopName)

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BusScheduleViewModel::class.java)) {
                val database = BusScheduleDatabase.getDatabase(application)
                val dao = database.busScheduleDao()
                return BusScheduleViewModel(application, dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}