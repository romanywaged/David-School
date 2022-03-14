package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import com.example.davidschool.database.model.ChildrenWithAttendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {
    fun getAllAttendancesWithChild(childId : Int): Flow<ChildrenWithAttendance> = flow {
        emit(databaseHelper.getAllAttendancesWithChild(childId))
    }.flowOn(Dispatchers.IO)

    fun deleteChild(child:Child) = flow {
        emit(databaseHelper.deleteChild(child))
    }.flowOn(Dispatchers.IO)

}