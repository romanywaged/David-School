package com.example.davidschool.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.ui.adapter.GetAllAttendancesInMeetingAdapter
import com.example.davidschool.ui.viewmodel.GetAllAttendancesInMeetingViewModel
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_gat_all_attendances_in_meeting.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GatAllAttendancesInMeetingActivity : AppCompatActivity() {


    private var meetingId:Int = 0
    private var meetingName:String = ""
    private lateinit var attendancesInMeetingAdapter:GetAllAttendancesInMeetingAdapter
    private val getAllAttendancesInMeetingViewModel:GetAllAttendancesInMeetingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gat_all_attendances_in_meeting)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName

        getAllAttendancesInMeeting()

    }

    private fun getAllAttendancesInMeeting()
    {
        getAllAttendancesInMeetingViewModel.getAllAttendancesInMeeting(meetingId)
        lifecycleScope.launchWhenStarted {
            getAllAttendancesInMeetingViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.SuccessGetAllAttendancesInMeeting ->{
                        setUpRecycleView(it.attendances)
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView(attendances : List<Attendance>)
    {
        if (attendances.isEmpty())
        {
            emptyList.visibility = View.VISIBLE
        }
        attendancesInMeetingAdapter = GetAllAttendancesInMeetingAdapter(this,attendances)
        allAttendancesInMeeting_rv.setHasFixedSize(true)
        allAttendancesInMeeting_rv.layoutManager = LinearLayoutManager(this)
        allAttendancesInMeeting_rv.adapter = attendancesInMeetingAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}