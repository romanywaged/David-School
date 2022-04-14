package com.example.davidschool.utils

import android.provider.ContactsContract
import com.example.davidschool.database.model.*

sealed class DataState  {
    object Loading : DataState()

    class  Failure(val msg:Throwable) : DataState()

    object Empty : DataState()

    object SuccessChildOperation : DataState()

    object SuccessAddMeetings : DataState()

    class  SuccessIdMeeting(val id : Int) : DataState()

    class  SuccessGetAllMeetings(val meetings: List<Khedma>) : DataState()

    class  SuccessGetAllChildren(val children: List<Child>) : DataState()

    class  SuccessGetAllChildrenInAttendance(val attendanceWithChildren: AttendanceWithChildren) : DataState()

    class  SuccessAddAttendanceDay(val id:Long) : DataState()

    object  SuccessAddAttendanceDayRef : DataState()

    class  SuccessGetAllAttendancesInMeeting(val attendances:List<Attendance>) : DataState()

    class SuccessGetAllAttendancesToChild(val childrenWithAttendance: ChildrenWithAttendance) : DataState()

    object SuccessDeleteAllChildren : DataState()

    object SuccessChildUpdated : DataState()

    object SuccessAttendancesDeleted : DataState()

    object SuccessUpdateTotalPoints : DataState()

    object SuccessDeleteAllMeetings : DataState()

    class SuccessGetChild(val child: Child) : DataState()

    class SuccessGetMessage(val message: String) : DataState()
}