package com.example.davidschool.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.davidschool.R
import com.example.davidschool.ui.viewmodel.UpdateTotalPointsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateTotalPointsActivity : AppCompatActivity() {

    private val updateTotalPointsViewModel:UpdateTotalPointsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_total_points)

    }
}