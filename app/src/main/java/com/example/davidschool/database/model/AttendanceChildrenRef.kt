package com.example.davidschool.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["Child_Id", "attendance_id"])
class AttendanceChildrenRef {
    var Child_Id: Int = 0
    var attendance_id: Int = 0
}
