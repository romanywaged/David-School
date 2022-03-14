package com.example.davidschool.ui.adapter.listener

import com.example.davidschool.database.model.Child
import de.hdodenhof.circleimageview.CircleImageView

interface OnChildClick {

    fun onChildClicked(child: Child, position:Int, circleImageView: CircleImageView?)
}