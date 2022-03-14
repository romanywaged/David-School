package com.example.davidschool.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.activity_update_child.*
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream

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
    private val updateChildViewModel:UpdateChildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_child)

        child = intent.getSerializableExtra("childObject") as Child
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        putFieldsInTexts()

        pickedImage = true

        commonMethod = CommonMethod(this)


        supportActionBar!!.title = meetingName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        update_icon.setOnClickListener {
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

        update_Add_btn.setOnClickListener {
            val childName = update_person_name.text.toString()
            val childPhone = update_phone.text.toString()
            val childParentPhone = update_child_parent_phone.text.toString()
            val childAddress = update_child_address.text.toString()
            val childAbouna = update_child_abouna.text.toString()
            val childSchoolYear = update_school_year.text.toString()

            validateToInsertChildIntoDatabase(childName, childPhone, childSchoolYear,  childParentPhone, childAddress, childAbouna)
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

        val imageBytes = Base64.decode(child.childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        update_icon.load(image) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
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
            if (update_phone.text!!.length != 11 || update_child_parent_phone.text!!.length != 11)
            {
                commonMethod.showMessage("اضف رقم تليفون حقيقى")
            }
            else
            {
                if (pickedImage) {

                    child.childName = childName
                    child.childPhone = childPhone
                    child.childSchoolYear = childSchoolYear
                    child.childAbouna = childAbouna
                    child.childAddress = childAddress
                    child.childParentPhone = childParentPhone
                    child.childPhoto = childPhoto
                    child.childClassId = meetingId

                    updateChild(child)
                }
                else{
                    commonMethod.showMessage("من فضلك اختر صورة")
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
        intent.putExtra("childModel", child)

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

    private fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}