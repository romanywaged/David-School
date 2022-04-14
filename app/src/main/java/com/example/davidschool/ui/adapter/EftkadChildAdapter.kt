package com.example.davidschool.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.listener.OnEftkadClickListener
import kotlinx.android.synthetic.main.eftkad_row.view.*

class EftkadChildAdapter (private val context: Context, private val children:List<Child>, private val onEftkadClickListener: OnEftkadClickListener) :
    RecyclerView.Adapter<EftkadChildAdapter.MyEftkadViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEftkadViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.eftkad_row, parent, false)
        return MyEftkadViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyEftkadViewHolder, position: Int) {
        val child = children[position]
        holder.childName.text = child.childName
        holder.childWhatsappIcon.setOnClickListener {
            onEftkadClickListener.onChildEftkadListener(child)
        }
    }

    override fun getItemCount(): Int {
        return children.size
    }

    class MyEftkadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childName = itemView.child_eftekad_row_child_name!!
        val childWhatsappIcon = itemView.whats_app_icon!!
    }

}