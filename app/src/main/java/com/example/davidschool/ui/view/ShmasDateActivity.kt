package com.example.davidschool.ui.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.davidschool.R
import com.example.davidschool.database.model.Child
import com.example.davidschool.ui.adapter.ShmasAndBirthdayAdapter
import com.example.davidschool.ui.adapter.listener.OnEftkadClickListener
import com.example.davidschool.ui.viewmodel.ShmasDateViewModel
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_shmas_date.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ShmasDateActivity : AppCompatActivity(), OnEftkadClickListener {
    private val shmasDateViewModel: ShmasDateViewModel by viewModels()
    private lateinit var children:ArrayList<Child>
    private var meetingName = ""
    private var meetingId = 0
    private var shmasDateMessage = ""
    private lateinit var birthdayAdapter: ShmasAndBirthdayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shmas_date)

        children = ArrayList()



        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")

        supportActionBar!!.title = meetingName

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        getShmasDateMessage()

    }

    private fun getShmasDateMessage() {
        shmasDateViewModel.getShmasDateMessage(meetingId)
        lifecycleScope.launchWhenStarted {
            shmasDateViewModel.stateFlowData.collect{
                when(it){
                    is DataState.SuccessGetMessage -> {
                        shmasDateMessage =  it.message
                    }
                    else ->{

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getAllChildren()
    }

    private fun getAllChildren() {
        shmasDateViewModel.getAllChildren(meetingId)
        lifecycleScope.launchWhenStarted {
            shmasDateViewModel.stateFlowResponse.collect {
                when(it){
                    is DataState.SuccessGetAllChildren ->{
                        children.addAll(it.children)
                        setUpRecycle(children)
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun setUpRecycle(children: java.util.ArrayList<Child>) {
        birthdayAdapter = ShmasAndBirthdayAdapter(this, children, this, false)
        shmas_dates_rv.setHasFixedSize(true)
        shmas_dates_rv.layoutManager = LinearLayoutManager(this)
        shmas_dates_rv.adapter = birthdayAdapter
    }

    override fun onChildEftkadListener(child: Child) {
        sendWhatsappMessage(child)
    }

    private fun sendWhatsappMessage(child: Child) {
        val phoneTxt = "02${child.childPhone}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$phoneTxt&text=$shmasDateMessage")
        startActivity(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}