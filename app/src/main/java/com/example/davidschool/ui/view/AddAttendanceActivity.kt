package com.example.davidschool.ui.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Attendance
import com.example.davidschool.database.model.AttendanceChildrenRef
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.AddChildrenInAttendanceAdapter
import com.example.davidschool.ui.adapter.listener.OnChildAttendanceClicked
import com.example.davidschool.ui.viewmodel.AttendanceViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_attendance.*
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class AddAttendanceActivity : AppCompatActivity(), OnChildAttendanceClicked {

    private var meetingId:Int = 0
    private var meetingName:String = ""
    private lateinit var childrenAdapter: AddChildrenInAttendanceAdapter
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private lateinit var childMap:HashMap<Int, Child>
    private lateinit var children : ArrayList<Child>
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

    }

    override fun onStart() {
        super.onStart()

        getAllChildren()

        prepareToInsertIntoDB()
    }

    private fun prepareToInsertIntoDB() {
        add_attendance_btn.setOnClickListener {
            if (childMap.size == 0)
            {
                commonMethod.showMessage("من فضلك اضف مخدومين الى قائمة الحضور")
            }
            else {
                if (children.size != childMap.size)
                {
                    alertDelete()
                }
                else {
                    insertAttendanceDay()
                }
            }
        }
    }

    private fun alertDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setIcon(R.drawable.circleschoollogo)
            .setMessage("يوجد اسماء لم تتم اضافتهم لقائمة حضور اليوم هل تريد تغييب هؤلاء")
            .setTitle(getString(R.string.app_name))
            .setPositiveButton("نعم اريد") { dialog, _ ->
                insertAttendanceDay()
                dialog.dismiss()
            }.setNegativeButton("الغاء"
            ) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
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
                        commonMethod.showMessage("تم اضافة الحضور بنجاح")
                        navigateToAllAttendancesDay()
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
                        children = it.children as ArrayList
                        setUpRecycleView(children)
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView(children: ArrayList<Child>)
    {
        childrenAdapter = AddChildrenInAttendanceAdapter(children, this, this)
        add_attendance_rv.setHasFixedSize(true)
        add_attendance_rv.layoutManager = LinearLayoutManager(this)
        add_attendance_rv.adapter = childrenAdapter
    }


    private fun getCurrentDate(): String {
        val calender: Calendar = Calendar.getInstance()

        return DateFormat.getDateInstance(DateFormat.FULL, Locale("AR")).format(calender.time)

    }

    override fun onTakeAttendanceClick(child: Child, position: Int, checkBox: CheckBox) {
        val id = childrenAdapter.getItemId(position).toInt()
        if (childMap.containsKey(id.toInt()))
        {
            childMap.remove(id)
            checkBox.isChecked = false
        }
        else{
            childMap[id] = child
            checkBox.isChecked = true
        }
    }

    private fun navigateToAllAttendancesDay(){
        val intent = Intent(this,GetAllAttendancesInMeetingActivity::class.java)
        intent.putExtra("meetingId", meetingId)
        intent.putExtra("meetingName", meetingName)
        startActivity(intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu!!.findItem(R.id.searchChildren)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                childrenAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}