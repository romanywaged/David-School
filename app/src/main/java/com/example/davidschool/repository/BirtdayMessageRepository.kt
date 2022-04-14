package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BirtdayMessageRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {

    suspend fun getBirthdayMessage(meetingId:Int): Flow<String> = flow {
        emit(databaseHelper.getBirthdayMessage(meetingId))
    }.flowOn(Dispatchers.IO)

    fun getAllChildren (meetId: Int) : Flow<List<Child>> = flow {
        emit(databaseHelper.getAllChildrenInMeeting(meetId))
    }.flowOn(Dispatchers.IO)
}