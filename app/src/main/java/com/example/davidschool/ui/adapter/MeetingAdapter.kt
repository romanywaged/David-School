package com.example.davidschool.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.ui.adapter.listener.OnMeetingClick
import kotlinx.android.synthetic.main.meeting_row.view.*

class MeetingAdapter constructor(private var context: Context, private var meetings:List<Khedma>, private var onMeetingClick: OnMeetingClick)
    : RecyclerView.Adapter<MeetingAdapter.MyMeetingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMeetingViewHolder {
        val row : View = LayoutInflater.from(context).inflate(R.layout.meeting_row, parent, false)
        return MyMeetingViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyMeetingViewHolder, position: Int) {
        holder.meetingNameTxt.text = meetings[position].meetingName
        holder.itemView.setOnClickListener {
            onMeetingClick.onClicked(meetings[position])
        }
    }

    override fun getItemCount(): Int {
       return meetings.size
    }


    class MyMeetingViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        var meetingNameTxt = itemView.choir_row_name!!
        var meetingCard = itemView.meeting_row_card!!


    }
}