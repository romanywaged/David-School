package com.example.davidschool.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import kotlinx.android.synthetic.main.child_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChildrenAdapter constructor(private val context: Context, private val children:ArrayList<Child>, private val onChildClick: OnChildClick)
    : RecyclerView.Adapter<ChildrenAdapter.MyChildrenViewHolder>(), Filterable{
    private lateinit var fade: Animation
    private val filterData:List<Child> = ArrayList(children)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenAdapter.MyChildrenViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.child_row,parent, false)
        return MyChildrenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildrenAdapter.MyChildrenViewHolder, position: Int) {
        fade = AnimationUtils.loadAnimation(context, R.anim.scale_anim)
        holder.childCard.animation = fade
        holder.childNameTxt.text = children[position].childName
        holder.childPhoneTxt.text = children[position].childPhone
        holder.childAddressTxt.text = children[position].childAddress
        holder.childSchoolYearTxt.text = children[position].childSchoolYear

        val imageBytes = Base64.decode(children[position].childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.childPhoto.load(image)

        holder.childCard.setOnClickListener {
            onChildClick.onChildClicked(children[position], position, holder.childPhoto)
        }
    }

    override fun getItemCount(): Int {
        return children.size
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


    class MyChildrenViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var childNameTxt = itemView.child_row_name!!
        var childPhoneTxt = itemView.child_row_child_phone!!
        var childAddressTxt = itemView.child_row_child_address!!
        var childSchoolYearTxt = itemView.child_row_school_year!!
        var childCard = itemView.child_row_card!!
        var childPhoto = itemView.child_row_child_icon!!
    }
}