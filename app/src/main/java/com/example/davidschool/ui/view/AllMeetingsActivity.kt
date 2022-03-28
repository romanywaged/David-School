package com.example.davidschool.ui.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.ui.adapter.MeetingAdapter
import com.example.davidschool.ui.adapter.listener.OnMeetingClick
import com.example.davidschool.ui.viewmodel.AllMeetingsViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_all_meetings.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AllMeetingsActivity : AppCompatActivity(), OnMeetingClick {

    private val allMeetingsViewModel: AllMeetingsViewModel by viewModels()
    private lateinit var meetingAdapter : MeetingAdapter
    private lateinit var commonMethod: CommonMethod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_meetings)

        commonMethod = CommonMethod(this)


        add_meeting_fab.setOnClickListener {
            val intent = Intent(this, AddMeetingActivity::class.java)
            startActivity(intent)
        }


        supportActionBar!!.title = "كل الفصول"

    }

    override fun onStart() {
        super.onStart()
        getAllMeeting()
    }

   private fun getAllMeeting()
   {
       allMeetingsViewModel.getAllMeetings()
       lifecycleScope.launchWhenStarted {
           allMeetingsViewModel.stateFlowResponse.collect {
               when(it)
               {
                   is DataState.SuccessGetAllMeetings ->{
                       setUpRecycleView(it.meetings)
                   }
                   else ->{

                   }
               }
           }
       }
   }

    private fun setUpRecycleView(meetings: List<Khedma>)
    {
        meetingAdapter = MeetingAdapter(this,meetings,this)
        meetings_rv.setHasFixedSize(true)
        meetings_rv.layoutManager = LinearLayoutManager(this)
        meetings_rv.adapter = meetingAdapter
    }

    override fun onClicked(meeting: Khedma) {
        val intent:Intent = Intent(this, MeetingDashboard::class.java)
        intent.putExtra("meetingId", meeting.meetingId)
        intent.putExtra("meetingName", meeting.meetingName)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll ->{
                alertDelete()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun alertDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setIcon(R.drawable.circleschoollogo)
            .setMessage("هل انت متأكد من ان تريد حذف جميع البيانات")
            .setTitle(getString(R.string.app_name))
            .setPositiveButton("اريد الحذف") { dialog, _ ->
//                deleteAllAttendances()
                dialog.dismiss()
            }.setNegativeButton("الغاء"
            ) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

}