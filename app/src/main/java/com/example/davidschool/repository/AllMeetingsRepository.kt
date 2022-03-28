package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Khedma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AllMeetingsRepository @Inject constructor(private var databaseHelper: DatabaseHelper) {

    fun getAllMeetings () : Flow<List<Khedma>> = flow {
        emit(databaseHelper.getAllMeetings())
    }.flowOn(Dispatchers.IO)

    fun deleteAllMeetings() = flow {
        emit(databaseHelper.deleteAllMeetings())
    }.flowOn(Dispatchers.IO)

    fun deleteMeeting(meeting:Khedma) = flow {
        emit(databaseHelper.deleteMetting(meeting))
    }

    fun updateMeeting(meeting: Khedma) = flow {
        emit(databaseHelper.updateMeeting(meeting))
    }

}