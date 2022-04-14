package com.example.davidschool.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.listener.OnEftkadClickListener
import kotlinx.android.synthetic.main.birthday_row.view.*

class ShmasAndBirthdayAdapter (private val context: Context, private val children:ArrayList<Child>,
                               private val onEftkadClickListener: OnEftkadClickListener, private val isBirthDateFlag:Boolean):RecyclerView.Adapter<ShmasAndBirthdayAdapter.MyShmasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShmasViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.birthday_row, parent, false)
        return MyShmasViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyShmasViewHolder, position: Int) {

        val imageBytes = Base64.decode(children[position].childPhoto, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.childImg.load(image)

        holder.childName.text = children[position].childName

        if (isBirthDateFlag){
            holder.childBirthOrShmasDate.text = children[position].childBirthdate
        }
        else
        {
            holder.childBirthOrShmasDate.text = children[position].childShmasDate
        }

        holder.whatsappIcon.setOnClickListener {
            onEftkadClickListener.onChildEftkadListener(children[position])
        }

    }

    override fun getItemCount(): Int {
        return children.size
    }

    class MyShmasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childImg = itemView.birthday_child_image!!
        val childName = itemView.birthday_child_name!!
        val childBirthOrShmasDate = itemView.birthday_child_date!!
        val whatsappIcon = itemView.birthday_whats_app_icon!!
    }
}