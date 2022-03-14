package com.example.davidschool.repository

import com.example.davidschool.database.DatabaseHelper
import com.example.davidschool.database.model.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateRepository @Inject constructor(private val databaseHelper: DatabaseHelper) {

    fun updateChild(child: Child) = flow {
        emit(databaseHelper.updateChild(child))
    }.flowOn(Dispatchers.IO)

}