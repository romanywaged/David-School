package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AllChildrenWithShmasDate @Inject constructor(private val databaseHelper: DatabaseHelper) {

    suspend fun getAllChildren(meetingId: Int) : Flow<List<Child>> = flow {
        emit(databaseHelper.getAllChildrenWithShmasDate(meetingId))
    }.flowOn(Dispatchers.IO)

    suspend fun getShmasDateMessage(meetingId: Int) : Flow<String> = flow {
        emit(databaseHelper.getShmasDateMessage(meetingId))
    }.flowOn(Dispatchers.IO)

}