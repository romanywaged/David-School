package com.example.davidschool.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.viewmodel.ProfileViewModel
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.activity_child_profile.*
import kotlinx.coroutines.flow.collect
import kotlin.math.sinh

@AndroidEntryPoint
class ChildProfileActivity : AppCompatActivity() {

    private lateinit var child:Child
    private val REQUEST_CALL = 1
    private val profileViewModel:ProfileViewModel by viewModels()
    private var lastAttend = ""
    private var numOfAttend = 0
    private var meetingName = ""
    private var meetingId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_profile)

        child = intent.getSerializableExtra("childModel") as Child
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        getAllAttendancesToThisChild(child.id)

        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        child_profile_img.load(image)

        child_profile_abouna.text = child.childAbouna
        child_profile_address.text = child.childAddress
        child_profile_child_number.text = child.childPhone
        child_profile_name.text = child.childName
        child_profile_school_year.text = child.childSchoolYear
        child_profile_parent_phone_number.text = getString(R.string.parent_phone_number)+": " + child.childParentPhone

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = child.childName

        child_profile_parent_phone_number.setOnClickListener {
            val phoneNumber = child_profile_parent_phone_number.text.toString()
            makeCall(phoneNumber)
        }

        child_profile_child_number.setOnClickListener {
            val phoneNumber = child_profile_child_number.text.toString()
            makeCall(phoneNumber)
        }

    }


    private fun makeCall(number:String) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL)
        } else {
            val dial = "tel:$number"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_update ->{
                moveToUpdate()
                true
            }

            R.id.item_delete ->{
                deleteChild(child)
                true
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getAllAttendancesToThisChild(childId:Int)
    {
        profileViewModel.getAllAttendancesToChild(childId)
        lifecycleScope.launchWhenStarted {
            profileViewModel.stateFlowResponse.collect {
                when(it){
                    is DataState.SuccessGetAllAttendancesToChild ->{
                        if (it.childrenWithAttendance.attendances.isNotEmpty()) {
                            numOfAttend = it.childrenWithAttendance.attendances.size
                            lastAttend = it.childrenWithAttendance.attendances[it.childrenWithAttendance.attendances.size - 1].attendanceDate
                            child_profile_last_attendance.text = lastAttend
                            child_profile_num_of_attendance.text = numOfAttend.toString()
                        }
                        else {
                            child_profile_last_attendance.text = " "
                            child_profile_num_of_attendance.text = "0"
                        }

                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun deleteChild(child: Child){
        profileViewModel.deleteChild(child)
        lifecycleScope.launchWhenStarted {
            profileViewModel.stateFlowData.collect {
                when(it)
                {
                    is DataState.SuccessChildOperation ->{
                        onBackPressed()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun moveToAllChildrenInMeeting() {
        val intent = Intent(this, AllChildrenInMeetingActivity::class.java)
        intent.putExtra("meetingId", meetingId)
        intent.putExtra("meetingName", meetingName)
        startActivity(intent)
        finish()
    }

    private fun moveToUpdate()
    {
        val intent = Intent(this, UpdateChildActivity::class.java)
        intent.putExtra("childObject", child)
        intent.putExtra("meetingName", meetingName)
        intent.putExtra("meetingId", meetingId)
        startActivity(intent)
        finish()
    }

}