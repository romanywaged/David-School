package com.example.davidschool.ui.adapter.listener

import android.widget.CheckBox
import com.example.davidschool.database.model.Child
import de.hdodenhof.circleimageview.CircleImageView

interface OnChildAttendanceClicked {

    fun onTakeAttendanceClick(child: Child, position:Int, checkBox: CheckBox)

}