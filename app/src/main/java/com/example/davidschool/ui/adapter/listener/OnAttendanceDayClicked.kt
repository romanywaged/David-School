package com.example.davidschool.ui.adapter.listener

import com.example.davidschool.database.model.Attendance

interface OnAttendanceDayClicked {

    fun onAttendanceDayClick(attendance: Attendance)

}