package com.example.davidschool.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.GetAllChildrenInAttendanceAdapter
import com.example.davidschool.ui.viewmodel.GetAllChildrenInAttendanceViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_get_all_children_in_attendance_day.*
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
class GetAllChildrenInAttendanceDay : AppCompatActivity() {
    private val getAllChildrenInAttendanceDay:GetAllChildrenInAttendanceViewModel by viewModels()
    private lateinit var childrenList:List<Child>
    private var meetingName = ""
    private var attendanceName = ""
    private var attendanceId = 0
    private lateinit var adapter: GetAllChildrenInAttendanceAdapter
    private lateinit var commonMethod: CommonMethod


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_all_children_in_attendance_day)

        meetingName = intent.extras!!.getString("meetingName").toString()
        attendanceId = intent.extras!!.getInt("attendanceId")
        attendanceName = intent.extras!!.getString("attendanceName").toString()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName

        childrenList = ArrayList()
        commonMethod = CommonMethod(this)

        getAllChildrenInAttendance()

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)

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


    private fun getAllChildrenInAttendance() {
        getAllChildrenInAttendanceDay.getAllChildren(attendanceId)
        lifecycleScope.launchWhenStarted {
            getAllChildrenInAttendanceDay.stateFlowResponse.collect {
                when(it){
                    is DataState.SuccessGetAllChildrenInAttendance ->{
                        childrenList = it.attendanceWithChildren.children
                        setupRecycle()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun setupRecycle() {
        if (childrenList.isEmpty())
        {
            all_children_in_attendance_emptyList.visibility = View.VISIBLE
        }
        adapter = GetAllChildrenInAttendanceAdapter(this, childrenList as ArrayList<Child>)
        all_children_in_attendance_day_rv.setHasFixedSize(true)
        all_children_in_attendance_day_rv.layoutManager = LinearLayoutManager(this)
        all_children_in_attendance_day_rv.adapter = adapter
    }

    private fun createExcellSheet() {

        val wb: Workbook = HSSFWorkbook()
        val sheet: Sheet = wb.createSheet("Name of sheet")
        sheet.horizontallyCenter = true
        var cell: Cell? = null
        val row: Row = sheet.createRow(0)
        cell = row.createCell(0)
        cell.setCellValue("اسم الولد")
        sheet.setColumnWidth(0, 15 * 500)
        cell = row.createCell(1)
        cell.setCellValue("تاريخ اليوم")
        sheet.setColumnWidth(1, 15 * 500)
        for (i in 1 until childrenList.size) {
            val row: Row = sheet.createRow(i)
            cell = row.createCell(0)
            cell.setCellValue(childrenList[i].childName)
            sheet.setColumnWidth(0, 15 * 500)
            cell = row.createCell(1)
            cell.setCellValue(attendanceName)
            sheet.setColumnWidth(1, 15 * 500)
        }
        val file = File(getExternalFilesDir(null),
            "$attendanceName.xls"
        )
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            wb.write(outputStream)
            commonMethod.showMessage("تم انشاء الشيت بتاريخ اليوم")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "حدث خطأ", Toast.LENGTH_LONG).show()
            try {
                outputStream!!.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.second_menu, menu)
        val searchItem = menu!!.findItem(R.id.searchChildren)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.createExcell -> {
                takePermissions()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}