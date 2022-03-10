package com.example.davidschool.ui.view

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.ChildrenAdapter
import com.example.davidschool.ui.adapter.OnChildClick
import com.example.davidschool.ui.viewmodel.GetAllChildrenInMeetingViewModel
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_all_children_in_meeting.*
import kotlinx.coroutines.flow.collect
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class AllChildrenInMeetingActivity : AppCompatActivity(), OnChildClick {

    private var meetingId:Int = 0
    private var meetingName:String = ""
    private lateinit var childrenAdapter: ChildrenAdapter
    private lateinit var childrenList: ArrayList<Child>
    private val getAllChildrenInMeetingViewModel:GetAllChildrenInMeetingViewModel by viewModels()
    private val code = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_children_in_meeting)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        childrenList = ArrayList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName

        getAllChildren()

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)

        takePermissions()


    }

    private fun takePermissions()
    {
        if (isPermissionGranted())
        {
            createExcellSheet()
        }
        else{
            takePermission()
        }
    }

    private fun isPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExternalStoragePermission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            readExternalStoragePermission == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun takePermission()
    {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 100)
            }catch (ex : Exception)
            {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 100)
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) , 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
                    if (Environment.isExternalStorageManager()){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                    } else{
                        takePermission()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            val readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (readExternalStorage) {
                Toast.makeText(this, "Permission Granted on 11", Toast.LENGTH_LONG).show()
            }else{
                takePermission()
            }
        }
    }




    private fun getAllChildren()
    {
        getAllChildrenInMeetingViewModel.getAllChildren(meetingId)
        lifecycleScope.launchWhenStarted {
            getAllChildrenInMeetingViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.SuccessGetAllChildren ->{
                        setUpRecycleView(it.children)
                        childrenList = it.children as ArrayList<Child>
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView(children: List<Child>)
    {
        childrenAdapter = ChildrenAdapter(this,children, this)
        children_in_meeting_rv.setHasFixedSize(true)
        children_in_meeting_rv.layoutManager = LinearLayoutManager(this)
        children_in_meeting_rv.adapter = childrenAdapter
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

    override fun onChildClicked(child: Child, position: Int, circleImageView: CircleImageView?) {

        var activityOptions: ActivityOptions? = null

        activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,circleImageView,"SharedName")

        val intent: Intent = Intent(this, ChildProfileActivity::class.java)
        intent.putExtra("childModel", child)
        startActivity(intent,activityOptions.toBundle())
    }

    private fun createExcellSheet() {

        val wb:Workbook = HSSFWorkbook()
        val sheet: Sheet = wb.createSheet("Name of sheet")
        sheet.horizontallyCenter = true
        var cell: Cell? = null
        val row: Row = sheet.createRow(0)
        cell = row.createCell(0)
        cell.setCellValue("اسم الولد")
        sheet.setColumnWidth(0, 15 * 500)
        cell = row.createCell(1)
        cell.setCellValue("رقم تليفون")
        sheet.setColumnWidth(1, 15 * 500)
        cell = row.createCell(2)
        cell.setCellValue("رقم تليفون الوالد")
        sheet.setColumnWidth(2, 15 * 500)
        cell = row.createCell(3)
        cell.setCellValue("عنوان الولد")
        sheet.setColumnWidth(3, 15 * 800)
        cell = row.createCell(4)
        cell.setCellValue("السنه الدراسيه")
        sheet.setColumnWidth(4, 15 * 500)
        cell = row.createCell(5)
        cell.setCellValue("اب الاعتراف")
        sheet.setColumnWidth(5, 15 * 500)
        for (i in 1 until childrenList.size) {
            val row: Row = sheet.createRow(i)
            cell = row.createCell(0)
            cell.setCellValue(childrenList[i].childName)
            sheet.setColumnWidth(0, 15 * 500)
            cell = row.createCell(1)
            cell.setCellValue(childrenList[i].childPhone)
            sheet.setColumnWidth(1, 15 * 500)
            cell = row.createCell(2)
            cell.setCellValue(childrenList[i].childParentPhone)
            sheet.setColumnWidth(2, 15 * 500)
            cell = row.createCell(3)
            cell.setCellValue(childrenList[i].childAddress)
            sheet.setColumnWidth(3, 15 * 800)
            cell = row.createCell(4)
            cell.setCellValue(childrenList[i].childSchoolYear)
            sheet.setColumnWidth(4, 15 * 500)
            cell = row.createCell(5)
            cell.setCellValue(childrenList[i].childAbouna)
            sheet.setColumnWidth(5, 15 * 500)
        }
        val file = File(getExternalFilesDir(null),
            meetingName + ".xls")
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            wb.write(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
            try {
                outputStream!!.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

    }


}