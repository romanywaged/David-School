package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.AttendanceWithChildren
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllChildrenInAttendanceRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {

    fun getAllChildrenInAttendance(dayID :Int) : Flow<List<AttendanceWithChildren>> = flow {
        emit(databaseHelper.getAllChildrenInAttendance(dayID))
    }.flowOn(Dispatchers.IO)

}