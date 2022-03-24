package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Khedma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddMeetingRepository @Inject constructor(private var databaseHelper: DatabaseHelper) {
    suspend fun insertMeeting(meeting: Khedma) = flow {
        emit(databaseHelper.insertMeeting(meeting))
    }.flowOn(Dispatchers.IO)

    suspend fun getLastMeetingId() : Flow<Int> = flow {
        emit(databaseHelper.getLastMeetingId())
    }.flowOn(Dispatchers.IO)
}