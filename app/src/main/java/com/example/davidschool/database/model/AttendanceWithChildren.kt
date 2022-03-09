package com.example.davidschool.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class AttendanceWithChildren(
    @Embedded
    val attendance: Attendance,
    @Relation(
        parentColumn = "attendance_id",
        entityColumn = "Child_Id",
        associateBy = Junction(AttendanceChildrenRef::class)
    )

    val children: List<Child>
)
