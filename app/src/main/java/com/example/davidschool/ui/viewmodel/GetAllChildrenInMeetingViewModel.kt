package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.repository.GetAllChildrenInMeetingRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetAllChildrenInMeetingViewModel @Inject
constructor(private val getAllChildrenInMeetingRepository: GetAllChildrenInMeetingRepository) : ViewModel() {

    private val responseStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun getAllChildren(meetingId: Int) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        getAllChildrenInMeetingRepository.getAllChildren(meetingId)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessGetAllChildren(it)
            }
    }

}