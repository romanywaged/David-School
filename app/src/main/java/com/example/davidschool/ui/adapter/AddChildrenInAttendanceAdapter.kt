package com.example.davidschool.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import kotlinx.android.synthetic.main.attendance_row.view.*

class AddChildrenInAttendanceAdapter constructor(private val children:List<Child>, private val context: Context, private val onChildClick: OnChildClick): RecyclerView.Adapter<AddChildrenInAttendanceAdapter.MyAttendanceViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAttendanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attendance_row, parent, false)
        return MyAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAttendanceViewHolder, position: Int) {
        holder.childRowName.text = children[position].childName
        holder.childRowCheckBox.setOnClickListener {
                onChildClick.onChildClicked(children[position], position, null)
        }
    }

    override fun getItemCount(): Int {
        return children.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyAttendanceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childRowName = itemView.child_attendance_row_child_name!!
        val childRowCheckBox = itemView.child_attendance_row_child_checkBox!!
    }
}