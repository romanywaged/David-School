package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllChildrenInMeetingRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {
    fun getAllChildren (meetId: Int) : Flow<List<Child>> = flow {
        emit(databaseHelper.getAllChildrenInMeeting(meetId))
    }.flowOn(Dispatchers.IO)

    fun deleteAllChildren(choirId:Int) = flow {
        emit(databaseHelper.deleteAllChildrenInMeeting(choirId))
    }.flowOn(Dispatchers.IO)
}