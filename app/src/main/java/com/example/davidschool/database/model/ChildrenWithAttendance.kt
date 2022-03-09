package com.example.davidschool.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ChildrenWithAttendance(
    @Embedded
    val child: Child,
    @Relation(
        parentColumn = "Child_Id",
        entityColumn = "attendance_id",
        associateBy = Junction(AttendanceChildrenRef::class)
    )

    val attendances: List<Attendance>
)