package com.example.davidschool.ui.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.viewmodel.UpdateChildViewModel
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
import kotlinx.android.synthetic.main.activity_update_child.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream
import java.util.*

@AndroidEntryPoint
class UpdateChildActivity : AppCompatActivity() {
    private lateinit var child: Child
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private lateinit var bitmap: Bitmap
    private var pickedImage: Boolean = false
    private var childPhoto:String = ""
    private var meetingName = ""
    private var meetingId = 0
    private lateinit var commonMethod:CommonMethod
    private var isShmas = false
    private var childStatus = ""
    private var childGender = ""
    private var childShmasDegree = ""
    private var shmasDateListener: DatePickerDialog.OnDateSetListener? = null
    private var birthdateListener: DatePickerDialog.OnDateSetListener? = null
    private var selectedDate  = ""
    private val updateChildViewModel:UpdateChildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_child)

        child = intent.getSerializableExtra("childObject") as Child
        commonMethod = CommonMethod(this)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        supportActionBar!!.title = meetingName

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        putFieldsInTexts()

        val genders = resources.getStringArray(R.array.Gender)
        val arrayAdapterGenders = ArrayAdapter(this, R.layout.dropdown_items, genders)
        update_child_gender_text.setAdapter(arrayAdapterGenders)

        val status = resources.getStringArray(R.array.Status)
        val arrayAdapterStatus = ArrayAdapter(this, R.layout.dropdown_items, status)
        update_child_shmas_or_not.setAdapter(arrayAdapterStatus)

        val shmasDegrees = resources.getStringArray(R.array.shmas_degree)
        val arrayAdapterShmasDegree = ArrayAdapter(this, R.layout.dropdown_items, shmasDegrees)
        update_child_shmas_degree_text.setAdapter(arrayAdapterShmasDegree)

        update_child_gender_text.addTextChangedListener(textWatcher)

        update_child_shmas_or_not.addTextChangedListener(shmasTextWatcher)

        update_child_birthday.inputType = 0

        update_child_shmas_date.inputType  = 0

        getDateforBirthDate()


        getDateforShmasDate()



        choose_gellary_update_child.setOnClickListener {
            galleryCheckPermission()
        }

        take_photo_update_child.setOnClickListener {
            cameraCheckPermission()
        }


        update_Add_btn.setOnClickListener {
            val childName = update_person_name.text.toString()
            val childPhone = update_phone.text.toString()
            val childParentPhone = update_child_parent_phone.text.toString()
            val childAddress = update_child_address.text.toString()
            val childAbouna = update_child_abouna.text.toString()
            val childSchoolYear = update_school_year.text.toString()
            val childBirthDate = update_child_birthday.text.toString()
            val childShmasBy = update_child_shmas_notes.text.toString()
            val childShmasDate = update_child_shmas_date.text.toString()
            val childShmasDegree = update_child_shmas_degree_text.text.toString()

            validateToInsertChildIntoDatabase(childName, childPhone, childSchoolYear,  childParentPhone,
                childAddress, childAbouna, childBirthDate, childShmasBy, childShmasDate, childShmasDegree)
        }

    }

    private fun putFieldsInTexts()
    {
        update_child_abouna.setText(child.childAbouna)
        update_child_address.setText(child.childAddress)
        update_child_parent_phone.setText(child.childParentPhone)
        update_phone.setText(child.childPhone)
        update_person_name.setText(child.childName)
        update_school_year.setText(child.childSchoolYear)
        childPhoto = child.childPhoto
        pickedImage = true
        update_child_birthday.setText(child.childBirthdate)
        update_child_gender_text.setText(child.childGender)
        childGender = child.childGender
        childStatus = child.childShmasOrNot
        if (child.childGender == "ولد"){
            update_child_shmas.visibility = View.VISIBLE
            update_child_shmas_or_not.setText(child.childShmasOrNot)
            if (child.childShmasOrNot == "تمت رسامته شماس") {
                linear_if_boy_shmas_update_child.visibility = View.VISIBLE
                update_child_shmas_degree_text.setText(child.childShmasDegree)
                update_child_shmas_date.setText(child.childShmasDate)
                update_child_shmas_notes.setText(child.childShmasBy)
                update_child_shmas_or_not.setText(child.childShmasOrNot)
                isShmas = true
            }
            else
            {
                linear_if_boy_shmas_update_child.visibility = View.INVISIBLE
            }
        }
        else
        {
            update_child_shmas.visibility = View.INVISIBLE
        }



        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        update_icon.load(image) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }

    private fun getDateforShmasDate() {

        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        update_child_shmas_date.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, android.app.AlertDialog.THEME_HOLO_LIGHT,
                shmasDateListener, currentYear, currentMonth, currentDay)
            datePickerDialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        shmasDateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

                val newMonth = month + 1

                selectedDate = if (newMonth < 10) {
                    "$year-0$newMonth-$dayOfMonth"
                } else {
                    "$year-$newMonth-$dayOfMonth"
                }
                update_child_shmas_date.setText(selectedDate)
                selectedDate = ""
            }
    }

    private fun getDateforBirthDate() {

        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR] - 3
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        calendar.set(currentYear, currentMonth, currentDay)
        update_child_birthday.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, android.app.AlertDialog.THEME_HOLO_LIGHT,
                birthdateListener, currentYear, currentMonth, currentDay)
            datePickerDialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        birthdateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

                val newMonth = month + 1

                selectedDate = if (newMonth < 10) {
                    "$year-0$newMonth-$dayOfMonth"
                } else {
                    "$year-$newMonth-$dayOfMonth"
                }
                update_child_birthday.setText(selectedDate)
                selectedDate = ""
            }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.toString() == "ولد"){
                childGender = s.toString()
                update_child_shmas.visibility = View.VISIBLE
            }
            else
            {
                update_child_shmas.visibility = View.GONE
                linear_if_boy_shmas_update_child.visibility = View.GONE
                childGender = s.toString()
                childStatus = s.toString()
                childShmasDegree = s.toString()
                isShmas = false
                update_child_shmas_or_not.text!!.clear()
                update_child_shmas_degree_text.text!!.clear()
                update_child_shmas_date.text!!.clear()
                update_child_shmas_notes.text!!.clear()
            }
        }
    }

    private val shmasTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.toString() == "تمت رسامته شماس"){
                childStatus = s.toString()
                linear_if_boy_shmas_update_child.visibility = View.VISIBLE
                isShmas = true
            }
            else
            {
                linear_if_boy_shmas_update_child.visibility = View.GONE
                isShmas = false
                update_child_shmas_degree_text.text!!.clear()
                update_child_shmas_date.text!!.clear()
                update_child_shmas_notes.text!!.clear()
            }
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
                Toast.makeText(this@UpdateChildActivity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
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


    private fun showRotationalDialogForPermission() {
        android.app.AlertDialog.Builder(this)
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
                    update_icon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    childPhoto = bitmapToString(bitmap)

                }

                GALLERY_REQUEST_CODE -> {

                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false)


                    update_icon.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    pickedImage = true
                    childPhoto = bitmapToString(bitmap)
                }

            }

        }
        else {
            pickedImage = false
        }

    }


    private fun validateToInsertChildIntoDatabase(
        childName: String,
        childPhone: String,
        childSchoolYear: String,
        childParentPhone: String,
        childAddress: String,
        childAbouna: String,
        childBirthDate: String,
        childShmasBy: String,
        childShmasDate: String,
        childShmasDegree: String,
    ) {
        if (childName.isEmpty() || childPhone.isEmpty() || childSchoolYear.isEmpty() ||
            childAddress.isEmpty() || childAbouna.isEmpty() || childParentPhone.isEmpty() ||
            childBirthDate.isEmpty() || childGender.isEmpty() || childStatus.isEmpty()
        ) {
            commonMethod.showMessage("اضف جميع البيانات")
        } else {
            if (update_phone.text!!.length != 11 || update_child_parent_phone.text!!.length != 11) {
                commonMethod.showMessage("اضف رقم تليفون حقيقى")
            } else {
                if (isShmas) {
                    if (childShmasBy.isEmpty() || childShmasDate.isEmpty() || childShmasDegree.isEmpty()) {
                        commonMethod.showMessage("اضف بيانات الرسامه")
                    } else {
                        if (pickedImage) {
                            if(childBirthDate != childShmasDate) {
                                child.childName = childName
                                child.childPhone = childPhone
                                child.childSchoolYear = childSchoolYear
                                child.childAbouna = childAbouna
                                child.childAddress = childAddress
                                child.childParentPhone = childParentPhone
                                child.childPhoto = childPhoto
                                child.childClassId = meetingId
                                child.childBirthdate = childBirthDate
                                child.childGender = childGender
                                child.childShmasBy = childShmasBy
                                child.childShmasDate = childShmasDate
                                child.childShmasDegree = childShmasDegree
                                child.childShmasOrNot = childStatus

                                updateChild(child)
                            }else{
                                commonMethod.showMessage("تاريخ الرسامه مماثل لتاريخ الميلاد !")
                            }
                        } else {
                            commonMethod.showMessage("من فضلك اختر صورة")
                        }

                    }
                }else{
                    if (pickedImage) {
                        child.childName = childName
                        child.childPhone = childPhone
                        child.childSchoolYear = childSchoolYear
                        child.childAbouna = childAbouna
                        child.childAddress = childAddress
                        child.childParentPhone = childParentPhone
                        child.childPhoto = childPhoto
                        child.childClassId = meetingId
                        child.childBirthdate = childBirthDate
                        child.childGender = childGender
                        child.childShmasOrNot= childStatus
                        child.childShmasBy = ""
                        child.childShmasDate = ""
                        child.childShmasDegree = ""

                        updateChild(child)
                    } else {
                        commonMethod.showMessage("من فضلك اختر صورة")
                    }
                }
            }
        }
    }

    private fun updateChild(child: Child) {
        updateChildViewModel.updateChild(child)
        lifecycleScope.launchWhenStarted {
            updateChildViewModel.stateFlowResponse.collect {
                when(it){
                    is DataState.SuccessChildUpdated ->{
                        commonMethod.showMessage("تم التعديل")
                        backToProfile()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun backToProfile() {
        val intent = Intent(this, ChildProfileActivity::class.java)
        intent.putExtra("meetingName", meetingName)
        intent.putExtra("meetingId", meetingId)
        intent.putExtra("childId", child.id)

        startActivity(intent)
        finish()
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

    override fun onBackPressed() {
        backToProfile()
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}