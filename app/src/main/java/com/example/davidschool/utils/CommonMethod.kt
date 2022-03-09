package com.example.davidschool.utils

import android.content.Context
import android.widget.Toast

class CommonMethod constructor(private var context: Context){

    fun showMessage(msg : String)
    {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}