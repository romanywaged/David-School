package com.example.davidschool.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import kotlinx.android.synthetic.main.attendance_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class GetAllChildrenInAttendanceAdapter
constructor(private val context: Context, private val children:ArrayList<Child>) :
    RecyclerView.Adapter<GetAllChildrenInAttendanceAdapter.MyChildrenAttendanceViewHolder>(),
    Filterable {
    private lateinit var fade: Animation
    private val filterData:List<Child> = ArrayList(children)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChildrenAttendanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.attendance_row, parent, false)
        return MyChildrenAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyChildrenAttendanceViewHolder, position: Int) {
        fade = AnimationUtils.loadAnimation(context, R.anim.scale_anim)
        holder.cardChild.animation = fade
        holder.childName.text = children[position].childName
        holder.checkBox.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return children.size
    }

    class MyChildrenAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val childName = itemView.child_attendance_row_child_name!!
        val checkBox = itemView.child_attendance_row_child_checkBox!!
        val cardChild = itemView.child_row_card!!
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
}