package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.AttendanceChildrenRef
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AttendanceRepository @Inject constructor(private val databaseHelper: DatabaseHelper){

    suspend fun insertAttendanceDay(attendance: Attendance) :Flow<Long> = flow {
        emit(databaseHelper.insertAttendanceDay(attendance))
    }.flowOn(Dispatchers.IO)

    suspend fun insertChildrenIAttendance(attendanceChildrenRef: List<AttendanceChildrenRef>) = flow {
        emit(databaseHelper.insertAttendanceRef(attendanceChildrenRef))
    }.flowOn(Dispatchers.IO)

    fun getAllChildren (meetId: Int) : Flow<List<Child>> = flow {
        emit(databaseHelper.getAllChildrenInMeeting(meetId))
    }.flowOn(Dispatchers.IO)

}