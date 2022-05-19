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
import kotlinx.android.synthetic.main.attendance_row.view.*
import java.util.*

class AddChildrenInAttendanceAdapter
constructor(private val children:ArrayList<Child>, private val context: Context, private val onChildClick: OnChildAttendanceClicked)
    : RecyclerView.Adapter<AddChildrenInAttendanceAdapter.MyAttendanceViewHolder> (),
    Filterable{

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
        holder.childRowCheckBox.setOnClickListener {
            onChildClick.onTakeAttendanceClick(children[position], position, holder.childRowCheckBox)
        }
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }


    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Child> = ArrayList<Child>()
            if (constraint.isEmpty()) {
                filteredList.addAll(filterData)
            } else {
                val filterDataPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (model in filterData) {
                    if (model.childName.lowercase().contains(filterDataPattern)) {
                        filteredList.add(model)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            children.clear()
            children.addAll(results.values as Collection<Child>)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return children.size
    }
    override fun getItemId(position: Int): Long {

        val itemID: Int = filterData.indexOf(children[position]) ?: position
        return itemID.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return filterData.indexOf(children[position]) ?: position
    }

    class MyAttendanceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val childRowName = itemView.child_attendance_row_child_name!!
        val childRowCheckBox = itemView.child_attendance_row_child_checkBox!!
        val cardRow = itemView.child_row_card!!
    }
}