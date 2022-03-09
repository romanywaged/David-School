package com.example.davidschool.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.davidschool.R
import com.example.davidschool.repository.GetAllAttendancesInMeetingRepository
import kotlinx.android.synthetic.main.activity_meeting_dashboard.*

class MeetingDashboard : AppCompatActivity() {
    private var meetingId:Int = 0
    private var meetingName:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_dashboard)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        collapsed_toolbar.title = meetingName


        add_child_linear.setOnClickListener {
            navigateMeetingActivities(AddChildActivity::class.java)
        }

        list_all_children_linear.setOnClickListener {
            navigateMeetingActivities(AllChildrenInMeetingActivity::class.java)
        }

        add_new_attendance.setOnClickListener {
            navigateMeetingActivities(AddAttendanceActivity::class.java)
        }

        list_attendance_linear.setOnClickListener {
            navigateMeetingActivities(GatAllAttendancesInMeetingActivity::class.java)
        }

    }

    private fun navigateMeetingActivities(activityClass: Class<*>){
        val intent = Intent(this, activityClass)
        intent.putExtra("meetingId", meetingId)
        intent.putExtra("meetingName", meetingName)
        startActivity(intent)
    }
}