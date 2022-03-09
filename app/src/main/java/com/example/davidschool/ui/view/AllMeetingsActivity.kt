package com.example.davidschool.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.ui.adapter.MeetingAdapter
import com.example.davidschool.ui.adapter.OnMeetingClick
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
}