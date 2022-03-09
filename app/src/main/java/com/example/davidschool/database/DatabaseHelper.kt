package com.example.davidschool.database

import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.AttendanceChildrenRef
import com.example.davidschool.database.model.Child
import com.example.davidschool.database.model.Khedma
import javax.inject.Inject


class DatabaseHelper @Inject constructor(private var databaseDao: DatabaseDao) {

    suspend fun insertChild(child: Child) = databaseDao.insertChild(child)

    suspend fun insertMeeting(meeting: List<Khedma>) = databaseDao.insertMeeting(meeting)

    suspend fun getAllChildrenInMeeting(meetingId : Int) = databaseDao.getAllChildrenInClass(meetingId)

    suspend fun getAllMeetings() = databaseDao.getAllMeetings()

    suspend fun getLastMeetingId() = databaseDao.getLastMeetingId()

    suspend fun getAllChildren(dayId: Int) = databaseDao.getAllChildrenInDay(dayId)

    suspend fun getAllAttendancesWithChild(childId: Int) = databaseDao.getAllAttendancesWithChild(childId)

    suspend fun getAllAttendancesInMeeting(meetingId: Int) = databaseDao.getAllAttendancesInMeeting(meetingId)

    suspend fun insertAttendanceDay(attendance: Attendance) :Long = databaseDao.insertAttendance(attendance)

    suspend fun insertAttendanceRef(attendanceChildrenRef: List<AttendanceChildrenRef>) = databaseDao.insertChildrenInAttendance(attendanceChildrenRef)

}