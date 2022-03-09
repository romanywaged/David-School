package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.repository.GetAllAttendancesInMeetingRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAllAttendancesInMeetingViewModel
@Inject constructor(private val getAllAttendancesInMeeting: GetAllAttendancesInMeetingRepository) : ViewModel(){

    private val responseStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun getAllAttendancesInMeeting(meetingId: Int) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        getAllAttendancesInMeeting.getAllAttendancesInMeeting(meetingId)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessGetAllAttendancesInMeeting(it)
            }
    }

}