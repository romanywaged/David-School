package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.repository.BirtdayMessageRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirthdayViewModel
@Inject constructor(private val birthdayMessageRepository: BirtdayMessageRepository) : ViewModel(){

    private val responseStateFlow: MutableStateFlow<DataState> = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun getAllChildren(meetingId: Int) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        birthdayMessageRepository.getAllChildren(meetingId)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessGetAllChildren(it)
            }
    }


    private val dataStateFlow: MutableStateFlow<DataState> = MutableStateFlow(DataState.Empty)
    val stateFlowData: StateFlow<DataState> = dataStateFlow

    fun getBirthdayMessage(meetingId: Int) = viewModelScope.launch {
        dataStateFlow.value = DataState.Loading
        birthdayMessageRepository.getBirthdayMessage(meetingId)
            .catch { e ->
                dataStateFlow.value = DataState.Failure(e)
            }
            .collect {
                dataStateFlow.value = DataState.SuccessGetMessage(it)
            }
    }
}