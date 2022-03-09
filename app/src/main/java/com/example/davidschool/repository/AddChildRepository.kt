package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddChildRepository @Inject constructor(private var databaseHelper: DatabaseHelper) {

    suspend fun insertChild(child: Child) = flow {
        emit(databaseHelper.insertChild(child))
    }.flowOn(Dispatchers.IO)

}