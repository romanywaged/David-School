package com.example.davidschool.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_table")
class Attendance {

    @ColumnInfo(name = "attendance_id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "attendance_name")
    var attendanceDate : String = ""

    @ColumnInfo(name = "meetingId")
    var meetingId : Int = 0

}