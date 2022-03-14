package com.example.davidschool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davidschool.database.model.Child
import com.example.davidschool.repository.ProfileRepository
import com.example.davidschool.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel(){

    private val responseStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowResponse: StateFlow<DataState> = responseStateFlow

    fun getAllAttendancesToChild(childId: Int) = viewModelScope.launch {
        responseStateFlow.value = DataState.Loading
        profileRepository.getAllAttendancesWithChild(childId)
            .catch { e ->
                responseStateFlow.value = DataState.Failure(e)
            }
            .collect {
                responseStateFlow.value = DataState.SuccessGetAllAttendancesToChild(it)
            }
    }

    private val dataStateFlow : MutableStateFlow<DataState>
            = MutableStateFlow(DataState.Empty)
    val stateFlowData: StateFlow<DataState> = dataStateFlow

    fun deleteChild(child:Child) = viewModelScope.launch {
        dataStateFlow.value = DataState.Loading
        profileRepository.deleteChild(child)
            .catch { e ->
                dataStateFlow.value = DataState.Failure(e)
            }
            .collect {
                dataStateFlow.value = DataState.SuccessChildOperation
            }
    }
}