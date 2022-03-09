package com.example.davidschool.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Attendance
import kotlinx.android.synthetic.main.attendances_in_meeting_row.view.*

class GetAllAttendancesInMeetingAdapter
constructor(private val context: Context, private val attendances:List<Attendance>) :
RecyclerView.Adapter<GetAllAttendancesInMeetingAdapter.MyAllAttendancesViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAllAttendancesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attendances_in_meeting_row, parent, false)
        return MyAllAttendancesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAllAttendancesViewHolder, position: Int) {
        holder.attendanceName.text = attendances[position].attendanceDate
    }

    override fun getItemCount(): Int {
        return attendances.size
    }
    class MyAllAttendancesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)  {

        val attendanceName = itemView.attendace_in_meeting_row_name!!

    }
}