package com.example.davidschool.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.davidschool.R
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.ui.viewmodel.AddMeetingViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_meeting.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class AddMeetingActivity : AppCompatActivity() {

    private val addMeetingViewModel: AddMeetingViewModel by viewModels()
    private lateinit var commonMethod: CommonMethod
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private lateinit var bitmap: Bitmap
    private var pickedImage: Boolean = false
    private var meetingPhoto:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meeting)


        commonMethod = CommonMethod(this)

        choose_gellary_add_meeting.setOnClickListener {
            galleryCheckPermission()
        }

        take_photo_tittle_add_meeting.setOnClickListener {
            cameraCheckPermission()
        }


        Add_btn_meeting.setOnClickListener {
            val meetingName = add_meeting_meetingName.text.toString()
            val meetingSchoolYear = add_meeting_meetingSchool_year.text.toString()
            val meetingShmasMessage = add_meeting_shmas_message.text.toString()
            val meetingBirthdayMessage = add_meeting_birthday_message.text.toString()
            val meetingEftkadMessage = add_meeting_eftekad_message.text.toString()

            validateInputstoAddMeeting(meetingName, meetingSchoolYear,
                meetingBirthdayMessage, meetingShmasMessage, meetingEftkadMessage)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }

    private fun validateInputstoAddMeeting(meetingName:String, meetingSchoolYear:String,
                                           meetingBirthdayMessage:String, meetingShmasMessage:String, meetingEftkad:String) {

        if (meetingName.isEmpty() || meetingSchoolYear.isEmpty() ||
            meetingBirthdayMessage.isEmpty() || meetingShmasMessage.isEmpty() || meetingEftkad.isEmpty())
        {
            commonMethod.showMessage("اضف كل البيانات من فضلك")
        }
        else{
            if (pickedImage){
                val meeting = Khedma()
                meeting.meetingName = meetingName
                meeting.meetingSchoolYear = meetingSchoolYear
                meeting.meetingPhoto = meetingPhoto
                meeting.birthdayMessage = meetingBirthdayMessage
                meeting.shmasMessage = meetingShmasMessage
                meeting.eftekadMessage = meetingEftkad
                saveMeetingIntoDatabase(meeting)
            }
            else{
                commonMethod.showMessage("اضف صورة من فضلك")
            }
        }

    }

    private fun saveMeetingIntoDatabase(meeting: Khedma) {
        addMeetingViewModel.addMeeting(meeting)
        lifecycleScope.launchWhenStarted {
            addMeetingViewModel.stateFlowResponse.collect {
                when(it){
                    is DataState.Loading ->{
                        enableAndDisableItems(true)
                    }

                    is DataState.SuccessAddMeetings ->{
                        commonMethod.showMessage("تم الاضافه")
                        enableAndDisableItems(false)
                        pickedImage = false
                        meetingPhoto = ""
                    }
                    else->{

                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun enableAndDisableItems(isLoading: Boolean) {
        if (isLoading){
            progress_Add_meeting.visibility = View.VISIBLE
            Add_btn_meeting.visibility = View.INVISIBLE
            add_meeting_meetingName.isEnabled = false
            add_meeting_meetingSchool_year.isEnabled = false
        }
        else{
            progress_Add_meeting.visibility = View.INVISIBLE
            Add_btn_meeting.visibility = View.VISIBLE
            add_meeting_meetingName.isEnabled = true
            add_meeting_meetingSchool_year.isEnabled = true
            meeting_icon.setImageDrawable(getDrawable(R.drawable.church))
            add_meeting_meetingName.text!!.clear()
            add_meeting_meetingSchool_year.text!!.clear()
        }
    }

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@AddMeetingActivity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }


    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    bitmap = data?.extras?.get("data") as Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false)



                    //we are using coroutine image loader (coil)
                    meeting_icon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    meetingPhoto = bitmabToString(bitmap)

                }

                GALLERY_REQUEST_CODE -> {

                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false)


                    meeting_icon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    meetingPhoto = bitmabToString(bitmap)
                }

            }

        }
        else {
            pickedImage = false
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

    private fun bitmabToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


}