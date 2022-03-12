package com.example.davidschool.database

import androidx.room.*
import com.example.davidschool.database.model.*


@Dao
interface DatabaseDao {

    //############################################ Children ###########################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child)

    @Query("SELECT * FROM child_table WHERE child_class_id = :class_id")
    suspend fun getAllChildrenInClass(class_id:Int) :List<Child>

    @Query("DELETE FROM child_table where child_class_id =:childId")
    suspend fun deleteAllChildrenInMeeting(childId: Int)

    //############################################ Meeting ###########################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: List<Khedma>)

    @Query("SELECT * FROM KHEDMA_TABLE")
    suspend fun getAllMeetings() : List<Khedma>

    @Query("select ifnull(max(khedma_id),0)+1 from khedma_table")
    suspend fun getLastMeetingId() : Int

    //############################################ Attendance ###########################################

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance):Long

    @Insert
    suspend fun insertChildrenInAttendance(attendanceChildrenRef: List<AttendanceChildrenRef>)

    @Transaction
    @Query("SELECT * FROM attendance_table where attendance_id =:dayId")
    suspend fun getAllChildrenInDay(dayId : Int) : List<AttendanceWithChildren>

    @Transaction
    @Query("SELECT * FROM child_table where Child_Id =:childId")
    suspend fun getAllAttendancesWithChild(childId : Int) : List<ChildrenWithAttendance>

    @Query("SELECT * FROM attendance_table where meetingId=:meetId")
    suspend fun getAllAttendancesInMeeting(meetId : Int) : List<Attendance>

}