package com.example.davidschool.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.listener.OnChildAttendanceClicked
import com.example.davidschool.ui.adapter.listener.OnChildClick
import kotlinx.android.synthetic.main.attendance_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class AddChildrenInAttendanceAdapter
constructor(private val children:ArrayList<Child>, private val context: Context, private val onChildClick: OnChildAttendanceClicked)
    : RecyclerView.Adapter<AddChildrenInAttendanceAdapter.MyAttendanceViewHolder> (){

    private val filterData:List<Child> = ArrayList(children)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAttendanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attendance_row, parent, false)
        return MyAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAttendanceViewHolder, position: Int) {
        holder.childRowName.text = children[position].childName
        holder.cardRow.setOnClickListener {
                onChildClick.onTakeAttendanceClick(children[position], position, holder.childRowCheckBox)
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
        val cardRow = itemView.child_row_card!!
    }
}