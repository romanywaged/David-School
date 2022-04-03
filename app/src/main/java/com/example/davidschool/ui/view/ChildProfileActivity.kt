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
import android.view.View
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
import kotlinx.android.synthetic.main.activity_child_profile.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ChildProfileActivity : AppCompatActivity() {

    private lateinit var child:Child
    private val REQUEST_CALL = 1
    private val profileViewModel:ProfileViewModel by viewModels()
    private var lastAttend = ""
    private var numOfAttend = 0
    private var meetingName = ""
    private var meetingId = 0
    private var childId = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_profile)

        childId = intent.extras!!.getInt("childId")
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        getChildById(childId)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        child_profile_parent_phone_number.setOnClickListener {
            makeCall(child.childParentPhone)
        }

        child_profile_child_number.setOnClickListener {
            makeCall(child.childPhone)
//            sendMessage()
        }

    }

    private fun getChildById(childId: Int) {
        profileViewModel.selectChild(childId)
        lifecycleScope.launchWhenStarted {
            profileViewModel.stateFlowSelectChild.collect {
                when(it){
                    is DataState.Failure ->{
                        Toast.makeText(this@ChildProfileActivity, "Nooo",Toast.LENGTH_LONG)
                    }

                    is DataState.SuccessGetChild ->{
                        child = it.child as Child
                        setUpChildViews(child)
                    }
                    else ->{

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpChildViews(child: Child)
    {
        supportActionBar!!.title = child.childName

        getAllAttendancesToThisChild(childId)

        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        child_profile_img.load(image)

        child_profile_abouna.text = child.childAbouna
        child_profile_address.text = child.childAddress
        child_profile_child_number.text = child.childPhone
        child_profile_name.text = child.childName
        child_profile_school_year.text = child.childSchoolYear
        child_profile_parent_phone_number.text = getString(R.string.parent_phone_number)+": " + child.childParentPhone
        child_profile_gender.text = child.childGender
        child_profile_num_of_points.text = getString(R.string.total_points)+": " + child.childPoints.toString()
        child_profile_shmas_by.text = getString(R.string.shmas_notes)+": " + child.childShmasBy
        child_profile_shmas_status.text = child.childShmasOrNot
        child_profile_shmas_date.text = getString(R.string.shmas_date)+": " + child.childShmasDate
        child_profile_birth_date.text  = getString(R.string.birthday_date)+": " + child.childBirthdate
        child_profile_shmas_degree.text = getString(R.string.shmas_type)+": " + child.childShmasDegree

        if (child.childGender == "ولد")
        {
            shmas_linear.visibility = View.VISIBLE
        }
        else
        {
            shmas_linear.visibility = View.GONE
        }
    }


    private fun makeCall(number:String) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL)
            val dial = "tel:$number"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))

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
                moveToUpdate(false)
                true
            }

            R.id.item_delete ->{
                deleteChild(child)
                true
            }

            R.id.item_update_points ->{
                moveToUpdate(true)
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


    private fun moveToUpdate(pointsFlag : Boolean)
    {

        if (pointsFlag) {
            val intent = Intent(this, UpdateTotalPointsActivity::class.java)
            intent.putExtra("childModel", child)
            intent.putExtra("meetingName", meetingName)
            intent.putExtra("meetingId", meetingId)
            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(this, UpdateChildActivity::class.java)
            intent.putExtra("childObject", child)
            intent.putExtra("meetingName", meetingName)
            intent.putExtra("meetingId", meetingId)
            startActivity(intent)
            finish()
        }
    }

    private fun sendMessage(){
        val phoneTxt = "02${child.childPhone}"
        val msgText = "Hello"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$phoneTxt&text=$msgText")
        startActivity(intent)
    }



}