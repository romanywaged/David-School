package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateTotalPointsRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {

    fun updateTotalPoints(points: Int, child_id: Int) = flow {
        emit(databaseHelper.updateTotalPoints(points, child_id))
    }.flowOn(Dispatchers.IO)

}