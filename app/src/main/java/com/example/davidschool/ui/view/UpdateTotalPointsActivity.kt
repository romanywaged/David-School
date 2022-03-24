package com.example.davidschool.ui.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import androidx.activity.viewModels
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.viewmodel.UpdateTotalPointsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_child_profile.*
import kotlinx.android.synthetic.main.activity_update_total_points.*

@AndroidEntryPoint
class UpdateTotalPointsActivity : AppCompatActivity() {
    private val updateTotalPointsViewModel:UpdateTotalPointsViewModel by viewModels()
    private var meetingName = ""
    private var meetingId = 0
    private lateinit var child:Child

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_total_points)



        child = intent.getSerializableExtra("childModel") as Child
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        child_update_points_img.load(image)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "تعديل النقاط"

        child_update_points_name.text = child.childName
        child_update_points_allPoints.text = "كل النقاط: "+child.childPoints



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