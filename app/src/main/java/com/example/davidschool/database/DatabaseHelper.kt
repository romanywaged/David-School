package com.example.davidschool.database

import com.example.davidschool.database.model.*
import javax.inject.Inject


class DatabaseHelper @Inject constructor(private var databaseDao: DatabaseDao) {

    suspend fun insertChild(child: Child) = databaseDao.insertChild(child)

    suspend fun insertMeeting(meeting:Khedma) = databaseDao.insertMeeting(meeting)

    suspend fun getAllChildrenInMeeting(meetingId : Int) = databaseDao.getAllChildrenInClass(meetingId)

    suspend fun getAllMeetings() = databaseDao.getAllMeetings()

    suspend fun getLastMeetingId() = databaseDao.getLastMeetingId()

    suspend fun deleteAllMeetings() = databaseDao.deleteAllMeetings()

    suspend fun deleteMetting(meeting: Khedma) = databaseDao.deleteMeeting(meeting)

    suspend fun updateMeeting(meeting: Khedma) = databaseDao.updateMeeting(meeting)

    suspend fun getAllChildrenInAttendance(dayId: Int):AttendanceWithChildren = databaseDao.getAllChildrenInDay(dayId)

    suspend fun getAllAttendancesWithChild(childId: Int): ChildrenWithAttendance = databaseDao.getAllAttendancesWithChild(childId)

    suspend fun getAllAttendancesInMeeting(meetingId: Int) = databaseDao.getAllAttendancesInMeeting(meetingId)

    suspend fun insertAttendanceDay(attendance: Attendance) :Long = databaseDao.insertAttendance(attendance)

    suspend fun insertAttendanceRef(attendanceChildrenRef: List<AttendanceChildrenRef>) = databaseDao.insertChildrenInAttendance(attendanceChildrenRef)

    suspend fun deleteAllChildrenInMeeting(choirId:Int) = databaseDao.deleteAllChildrenInMeeting(choirId)

    suspend fun deleteChild(child:Child) = databaseDao.deleteChild(child)

    suspend fun updateChild(child: Child) = databaseDao.updateChild(child)

    suspend fun getChildById(childId:Int) = databaseDao.getChildById(childId)

    suspend fun deleteAllAttendancesInMeeting(meetId: Int) = databaseDao.deleteAllattendancesInMeeting(meetId)

    suspend fun updateTotalPoints(points : Int, childId:Int) = databaseDao.updateTotalPoints(points, childId)

    suspend fun getBirthdayMessage(meetingID: Int)  = databaseDao.getBirthdayMessage(meetingID)

    suspend fun getShmasDateMessage(meetingID: Int)  = databaseDao.getShmasDateMessage(meetingID)

    suspend fun getAllChildrenWithShmasDate(meetingID: Int)  = databaseDao.getAllChildrenWithShmasDate(meetingID)
}