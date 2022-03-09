package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.repository.AddMeetingRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMeetingViewModel
@Inject constructor(private var addMeetingRepository: AddMeetingRepository) : ViewModel() {

    private val responseStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun addMeeting(meetings : List<Khedma>) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        addMeetingRepository.insertMeeting(meetings)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessAddChild
            }
    }

    private val dataStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowData: StateFlow<DataState> = dataStateFlow

    fun getLastMeetingId() = viewModelScope.launch {
        dataStateFlow.value = DataState.Loading
        addMeetingRepository.getLastMeetingId()
            .catch { e ->
                dataStateFlow.value = DataState.Failure(e)
            }
            .collect {
                dataStateFlow.value = DataState.SuccessIdMeeting(it)
            }
    }

}
