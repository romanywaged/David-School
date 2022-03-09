package com.example.davidschool.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.activity_child_profile.*


class ChildProfileActivity : AppCompatActivity() {

    private lateinit var child:Child
    private val REQUEST_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_profile)

        child = intent.getSerializableExtra("childModel") as Child


        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        child_profile_img.load(image)

        child_profile_abouna.text = child.childAbouna
        child_profile_address.text = child.childAddress
        child_profile_child_number.text = child.childPhone
        child_profile_name.text = child.childName
        child_profile_school_year.text = child.childSchoolYear
        child_profile_parent_phone_number.text = child.childParentPhone

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
}