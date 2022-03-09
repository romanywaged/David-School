package com.example.davidschool.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "khedma_table")
data class Khedma(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "khedma_id")
    var meetingId: Int,


    @ColumnInfo(name = "khedma_name")
    var meetingName: String
)
