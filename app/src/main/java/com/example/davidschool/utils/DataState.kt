package com.example.davidschool.utils

import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.Child
import com.example.davidschool.database.model.Khedma

sealed class DataState  {
    object Loading : DataState()

    class  Failure(val msg:Throwable) : DataState()

    object Empty : DataState()

    object SuccessAddChild : DataState()

    object SuccessAddMeetings : DataState()

    class  SuccessIdMeeting(val id : Int) : DataState()

    class  SuccessGetAllMeetings(val meetings: List<Khedma>) : DataState()

    class  SuccessGetAllChildren(val children: List<Child>) : DataState()

    class  SuccessAddAttendanceDay(val id:Long) : DataState()

    object  SuccessAddAttendanceDayRef : DataState()

    class  SuccessGetAllAttendancesInMeeting(val attendances:List<Attendance>) : DataState()

    object SuccessDeleteAllChildren : DataState()
}