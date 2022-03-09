package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Attendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllAttendancesInMeetingRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {

    suspend fun getAllAttendancesInMeeting(meetingId:Int): Flow<List<Attendance>> = flow {
        emit(databaseHelper.getAllAttendancesInMeeting(meetingId))
    }.flowOn(Dispatchers.IO)

}