package com.example.davidschool.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.viewmodel.UpdateTotalPointsViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_child_profile.*
import kotlinx.android.synthetic.main.activity_update_total_points.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UpdateTotalPointsActivity : AppCompatActivity() {
    private val updateTotalPointsViewModel:UpdateTotalPointsViewModel by viewModels()
    private var meetingName = ""
    private var meetingId = 0
    private lateinit var child:Child
    private lateinit var commonMethod: CommonMethod

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_total_points)



        child = intent.getSerializableExtra("childModel") as Child
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        commonMethod = CommonMethod(this)

        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        child_update_points_img.load(image)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "تعديل النقاط"

        child_update_points_name.text = child.childName
        child_update_points_allPoints.text = "كل النقاط: ${child.childPoints}"

        child_update_points_edit.setOnClickListener {
            if (child_update_points_write_points.text.toString().isNotEmpty()){
                val points = child_update_points_write_points.text.toString().toInt()
                val prevPoints = child_update_points_allPoints.text.toString().split(": ")[1].toInt()
                val totPoints = points  + prevPoints
                updateTotalPoints(totPoints, child.id)
            }
            else{
                commonMethod.showMessage("من فضلك ضع النقاط الجديده")
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPoints(points:Int, childId:Int) {
        updateTotalPointsViewModel.updateTotalPoints(points, childId)
        lifecycleScope.launchWhenStarted {
            updateTotalPointsViewModel.stateFlowResponse.collect {
                when (it)
                {
                    is DataState.SuccessUpdateTotalPoints -> {
                        commonMethod.showMessage("تم تعديل النقاط")
                        child_update_points_allPoints.text = "كل النقاط: $points"
                        child_update_points_write_points.text!!.clear()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startChildProfile()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startChildProfile(){
        val intent = Intent(this, ChildProfileActivity::class.java)
        intent.putExtra("meetingName", meetingName)
        intent.putExtra("childId", child.id)
        intent.putExtra("meetingId", meetingId)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        startChildProfile()
    }
}