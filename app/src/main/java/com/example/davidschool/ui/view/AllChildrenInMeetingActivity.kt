package com.example.davidschool.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
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
import com.example.davidschool.ui.adapter.listener.OnChildClick
import com.example.davidschool.ui.viewmodel.GetAllChildrenInMeetingViewModel
import com.example.davidschool.utils.CommonMethod
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
    private lateinit var commonMethod: CommonMethod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_children_in_meeting)

        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        childrenList = ArrayList()
        commonMethod = CommonMethod(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName


        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED)

    }

    override fun onStart() {
        super.onStart()
        getAllChildren()
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

    private fun getAllChildren()
    {
        getAllChildrenInMeetingViewModel.getAllChildren(meetingId)
        lifecycleScope.launchWhenStarted {
            getAllChildrenInMeetingViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.SuccessGetAllChildren ->{
                        setUpRecycleView(it.children as ArrayList<Child>)
                        childrenList = it.children
                    }
                    else ->{

                    }
                }
            }
        }
    }
    private fun setUpRecycleView(children: ArrayList<Child>)
    {
        childrenAdapter = ChildrenAdapter(this,children, this)
        children_in_meeting_rv.setHasFixedSize(true)
        children_in_meeting_rv.layoutManager = LinearLayoutManager(this)
        children_in_meeting_rv.adapter = childrenAdapter
    }


    override fun onChildClicked(child: Child, position: Int, circleImageView: CircleImageView?) {
        val intent: Intent = Intent(this, ChildProfileActivity::class.java)
        intent.putExtra("childId", child.id)
        intent.putExtra("meetingName", meetingName)
        intent.putExtra("meetingId", meetingId)
        startActivity(intent)
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
        sheet.setColumnWidth(3, 20 * 800)
        cell = row.createCell(4)
        cell.setCellValue("السنه الدراسيه")
        sheet.setColumnWidth(4, 15 * 500)
        cell = row.createCell(5)
        cell.setCellValue("اب الاعتراف")
        sheet.setColumnWidth(5, 15 * 500)
        for (i in 0 until childrenList.size) {
            val row: Row = sheet.createRow(i+1)
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
            sheet.setColumnWidth(3, 20 * 800)
            cell = row.createCell(4)
            cell.setCellValue(childrenList[i].childSchoolYear)
            sheet.setColumnWidth(4, 15 * 500)
            cell = row.createCell(5)
            cell.setCellValue(childrenList[i].childAbouna)
            sheet.setColumnWidth(5, 15 * 500)
        }
        val file = File(getExternalFilesDir(null),
            "$meetingName.xls"
        )
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            wb.write(outputStream)
            commonMethod.showMessage("تم انشاء الشيت بأسم المجموعه")
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
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu!!.findItem(R.id.searchChildren)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                childrenAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAll -> {
                alertDelete()
                return true
            }
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

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteAllChildrenInMeeting(choirId :Int)
    {
        getAllChildrenInMeetingViewModel.deleteAllChildren(choirId)
        lifecycleScope.launchWhenStarted {
            getAllChildrenInMeetingViewModel.stateFlowData.collect {
                when(it)
                {
                    is DataState.SuccessDeleteAllChildren ->{
                        commonMethod.showMessage("تم المسح")
                        childrenList.clear()
                        childrenAdapter.notifyDataSetChanged()
                    }
                    else ->{

                    }
                }
            }
        }
    }


    private fun alertDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setIcon(R.drawable.circleschoollogo)
            .setMessage("هل انت متأكد من ان تريد حذف جميع البيانات")
            .setTitle(getString(R.string.app_name))
            .setPositiveButton("اريد الحذف") { dialog, _ ->
                deleteAllChildrenInMeeting(meetingId)
                dialog.dismiss()
            }.setNegativeButton("الغاء"
            ) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

}