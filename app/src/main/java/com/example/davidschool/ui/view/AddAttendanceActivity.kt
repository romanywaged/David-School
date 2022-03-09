package com.example.davidschool.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.AttendanceChildrenRef
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.AddChildrenInAttendanceAdapter
import com.example.davidschool.ui.adapter.OnChildClick
import com.example.davidschool.ui.viewmodel.AttendanceViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_add_attendance.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class AddAttendanceActivity : AppCompatActivity(), OnChildClick{

    private var meetingId:Int = 0
    private var meetingName:String = ""
    private lateinit var childrenAdapterAddChildrenIn: AddChildrenInAttendanceAdapter
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private lateinit var childMap:HashMap<Int, Child>
    private lateinit var commonMethod: CommonMethod
    private lateinit var attendanceChildrenRefList: ArrayList<AttendanceChildrenRef>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attendance)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")
        childMap = HashMap()
        commonMethod = CommonMethod(this)
        attendanceChildrenRefList = ArrayList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName


        getAllChildren()

        prepareToInsertIntoDB()
    }

    private fun prepareToInsertIntoDB() {
        add_attendance_btn.setOnClickListener {
            if (childMap.size == 0)
            {
                commonMethod.showMessage("من فضلك اضف مخدومين الى قائمة الحضور")
            }
            else{
                insertIntoDB()
            }
        }
    }

    private fun insertIntoDB() {
        insertAttendanceDay()

    }

    private fun insertChildrenInAttendanceDay(id: Long) {
        for ((key,value) in childMap)
        {
            val attendanceChildrenRef = AttendanceChildrenRef()
            attendanceChildrenRef.attendance_id = id.toInt()
            attendanceChildrenRef.Child_Id = value.id
            attendanceChildrenRefList.add(attendanceChildrenRef)
        }

        attendanceViewModel.addAttendanceDayRef(attendanceChildrenRefList)
        lifecycleScope.launchWhenStarted {
            attendanceViewModel.stateFlowAddRef.collect {
                when(it)
                {
                    is DataState.Loading -> {

                    }
                    is DataState.SuccessAddAttendanceDayRef ->{
                        commonMethod.showMessage("Success")
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun insertAttendanceDay() {
        val attendance:Attendance = Attendance()
        attendance.attendanceDate = getCurrentDate()
        attendance.meetingId = meetingId
        attendanceViewModel.addAttendanceDay(attendance)
        lifecycleScope.launchWhenStarted {
            attendanceViewModel.stateFlowData.collect {
                when (it){
                    is DataState.SuccessAddAttendanceDay -> {
                        insertChildrenInAttendanceDay(it.id)
                    }

                    else ->{

                    }
                }
            }
        }
    }


    private fun getAllChildren()
    {
        attendanceViewModel.getAllChildren(meetingId)
        lifecycleScope.launchWhenStarted {
            attendanceViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.SuccessGetAllChildren ->{
                        setUpRecycleView(it.children)
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView(children: List<Child>)
    {
        childrenAdapterAddChildrenIn = AddChildrenInAttendanceAdapter(children, this, this)
        add_attendance_rv.setHasFixedSize(true)
        add_attendance_rv.layoutManager = LinearLayoutManager(this)
        add_attendance_rv.adapter = childrenAdapterAddChildrenIn
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


    private fun getCurrentDate() : String
    {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        return currentDate

    }

    override fun onChildClicked(child: Child, position: Int, circleImageView: CircleImageView?) {
        if (childMap.containsKey(position))
        {
            childMap.remove(position)
        }
        else{
            childMap[position] = child
        }
    }


}