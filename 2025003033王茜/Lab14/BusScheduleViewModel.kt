package com.example.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.busschedule.data.BusSchedule
import com.example.busschedule.data.BusScheduleDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class BusScheduleViewModel(
    private val dao: BusScheduleDao
) : ViewModel() {

    fun getAllSchedules(): Flow<List<BusSchedule>> =
        dao.getAllSchedules()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun getSchedulesByStop(stopName: String): Flow<List<BusSchedule>> =
        dao.getByStopName(stopName)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}

class BusScheduleViewModelFactory(
    private val dao: BusScheduleDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusScheduleViewModel::class.java)) {
            return BusScheduleViewModel(dao) as T
        }
        throw IllegalArgumentException("错了！")
    }
}