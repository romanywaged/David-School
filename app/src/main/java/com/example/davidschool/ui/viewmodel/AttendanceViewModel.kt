package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.AttendanceChildrenRef
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.repository.AttendanceRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val attendanceRepository: AttendanceRepository) : ViewModel() {

    private val responseStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun getAllChildren(meetingId: Int) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        attendanceRepository.getAllChildren(meetingId)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessGetAllChildren(it)
            }
    }

    private val dataStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowData: StateFlow<DataState> = dataStateFlow

    fun addAttendanceDay(attendance: Attendance)  = viewModelScope.launch {
        dataStateFlow.value = DataState.Loading
        attendanceRepository.insertAttendanceDay(attendance)
            .catch { e ->
                dataStateFlow.value = DataState.Failure(e)
            }
            .collect {
                dataStateFlow.value = DataState.SuccessAddAttendanceDay(it)
            }
    }


    private val addRefStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowAddRef: StateFlow<DataState> = addRefStateFlow

    fun addAttendanceDayRef(attendanceChildrenRef: List<AttendanceChildrenRef>) = viewModelScope.launch {
        addRefStateFlow.value = DataState.Loading
        attendanceRepository.insertChildrenIAttendance(attendanceChildrenRef)
            .catch { e ->
                addRefStateFlow.value = DataState.Failure(e)
            }
            .collect {
                addRefStateFlow.value = DataState.SuccessAddAttendanceDayRef
            }
    }

}