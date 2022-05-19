package com.example.davidschool.ui.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.ui.adapter.GetAllAttendancesInMeetingAdapter
import com.example.davidschool.ui.adapter.listener.OnAttendanceDayClicked
import com.example.davidschool.ui.viewmodel.GetAllAttendancesInMeetingViewModel
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_gat_all_attendances_in_meeting.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GetAllAttendancesInMeetingActivity : AppCompatActivity(), OnAttendanceDayClicked {


    private var meetingId:Int = 0
    private var meetingName:String = ""
    private lateinit var attendancesInMeetingAdapter:GetAllAttendancesInMeetingAdapter
    private val getAllAttendancesInMeetingViewModel:GetAllAttendancesInMeetingViewModel by viewModels()
    private lateinit var attendances: ArrayList<Attendance>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gat_all_attendances_in_meeting)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName


        attendances = ArrayList()

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
                        attendances = it.attendances as ArrayList<Attendance>
                        setUpRecycleView()
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView()
    {
        if (attendances.isEmpty())
        {
            emptyList.visibility = View.VISIBLE
        }
        attendancesInMeetingAdapter = GetAllAttendancesInMeetingAdapter(this,attendances, this)
        allAttendancesInMeeting_rv.setHasFixedSize(true)
        allAttendancesInMeeting_rv.layoutManager = LinearLayoutManager(this)
        allAttendancesInMeeting_rv.adapter = attendancesInMeetingAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll ->{
                alertDelete()
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllAttendances() {
        getAllAttendancesInMeetingViewModel.deleteAllAttendancesInMeeting(meetingId)
        lifecycleScope.launchWhenStarted {
            getAllAttendancesInMeetingViewModel.stateFlowData.collect {
                when(it){
                    is DataState.SuccessAttendancesDeleted ->{
                        attendances.clear()
                        attendancesInMeetingAdapter.notifyDataSetChanged()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    override fun onAttendanceDayClick(attendance: Attendance) {
        val intent = Intent(this,GetAllChildrenInAttendanceDay::class.java)
        intent.putExtra("attendanceId", attendance.id)
        intent.putExtra("meetingName", meetingName)
        intent.putExtra("attendanceName", attendance.attendanceDate)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_nenu, menu)
        return true
    }

    private fun alertDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setIcon(R.drawable.circleschoollogo)
            .setMessage("هل انت متأكد من ان تريد حذف جميع البيانات")
            .setTitle(getString(R.string.app_name))
            .setPositiveButton("اريد الحذف") { dialog, _ ->
                deleteAllAttendances()
                dialog.dismiss()
            }.setNegativeButton("الغاء"
            ) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}