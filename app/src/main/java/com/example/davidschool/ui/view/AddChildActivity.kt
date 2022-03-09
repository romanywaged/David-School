package com.example.davidschool.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.viewmodel.AddChildViewModel
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
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class AddChildActivity : AppCompatActivity() {

    private var meetingName = ""
    private var meetingId = 0
    private val addChildViewModel:AddChildViewModel by viewModels()
    private lateinit var commonMethod: CommonMethod
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private lateinit var bitmap: Bitmap
    private var pickedImage: Boolean = false
    private var childPhoto:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)

        commonMethod = CommonMethod(this)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        supportActionBar!!.title = meetingName

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        icoon.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Select photo from Gallery",
                "Capture photo from Camera")
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 -> gallery()
                    1 -> camera()
                }
            }

            pictureDialog.show()
        }

        Add_btn.setOnClickListener {
            val childName = person_name.text.toString()
            val childPhone = phone.text.toString()
            val childParentPhone = child_parent_phone.text.toString()
            val childAddress = child_address.text.toString()
            val childAbouna = child_abouna.text.toString()
            val childSchoolYear = school_year.text.toString()

            validateToInsertChildIntoDatabase(childName, childPhone, childSchoolYear,  childParentPhone, childAddress, childAbouna)
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
                commonMethod.showMessage("Permission Denied!")
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?,
            ) {
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
                        p1: PermissionToken?,
                    ) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
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
                    icoon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    childPhoto = bitmabToString(bitmap)

                }

                GALLERY_REQUEST_CODE -> {

                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false)


                    icoon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    childPhoto = bitmabToString(bitmap)
                }

            }

        }
        else {
            pickedImage = false
        }

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


    private fun validateToInsertChildIntoDatabase(
        childName: String, childPhone: String, childSchoolYear: String,
        childParentPhone: String, childAddress: String, childAbouna: String,
    )
    {
        if (childName.isEmpty() || childPhone.isEmpty() || childSchoolYear.isEmpty() ||
                childAddress.isEmpty() || childAbouna.isEmpty() || childParentPhone.isEmpty())
        {
            commonMethod.showMessage("اضف جميع البيانات")
        }
        else
        {
            if (phone.text!!.length != 11 || child_parent_phone.text!!.length != 11)
            {
                commonMethod.showMessage("اضف رقم تليفون حقيقى")
            }
            else
            {
                if (pickedImage) {
                    val child = Child()
                    child.childName = childName
                    child.childPhone = childPhone
                    child.childSchoolYear = childSchoolYear
                    child.childAbouna = childAbouna
                    child.childAddress = childAddress
                    child.childParentPhone = childParentPhone
                    child.childPhoto = childPhoto
                    child.childClassId = meetingId

                    insertChildIntoDatabase(child)
                }
                else{
                    commonMethod.showMessage("من فضلك اختر صورة")
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun insertChildIntoDatabase(child: Child) {
        addChildViewModel.addChild(child)
        lifecycleScope.launchWhenStarted {
            addChildViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.Loading ->{
                        progress_Add.visibility = View.VISIBLE
                        Add_btn.visibility = View.GONE
                        person_name.isEnabled = false
                        phone.isEnabled = false
                        school_year.isEnabled = false
                        child_parent_phone.isEnabled = false
                        child_address.isEnabled = false
                        child_abouna.isEnabled = false
                    }
                    is DataState.SuccessAddChild -> {
                        commonMethod.showMessage("Success")
                        person_name.text!!.clear()
                        phone.text!!.clear()
                        school_year.text!!.clear()
                        child_parent_phone.text!!.clear()
                        child_address.text!!.clear()
                        child_abouna.text!!.clear()
                        person_name.isEnabled = true
                        phone.isEnabled = true
                        school_year.isEnabled = true
                        child_parent_phone.isEnabled = true
                        child_address.isEnabled = true
                        child_abouna.isEnabled = true
                        icoon.setImageDrawable(getDrawable(R.drawable.circleschoollogo))
                        progress_Add.visibility = View.GONE
                        Add_btn.visibility = View.VISIBLE

                    }
                    else ->
                    {

                    }
                }
            }
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

    fun bitmabToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}