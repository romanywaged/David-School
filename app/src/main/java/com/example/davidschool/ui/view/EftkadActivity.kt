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
import com.example.davidschool.ui.adapter.ChildrenAdapter
import com.example.davidschool.ui.adapter.EftkadChildAdapter
import com.example.davidschool.ui.adapter.listener.OnEftkadClickListener
import com.example.davidschool.ui.viewmodel.EftkadViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_all_children_in_meeting.*
import kotlinx.android.synthetic.main.activity_eftkad.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EftkadActivity : AppCompatActivity(), OnEftkadClickListener{

    private val eftkadViewModel: EftkadViewModel by viewModels()
    private lateinit var children:List<Child>
    private var meetingName:String = ""
    private var meetingId:Int = 0
    private lateinit var commonMethod: CommonMethod
    private lateinit var eftkadAdapter: EftkadChildAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eftkad)
        meetingName = intent.extras!!.getString("meetingName").toString()
        meetingId = intent.extras!!.getInt("meetingId")


        commonMethod = CommonMethod(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = meetingName

        children = ArrayList()
    }

    override fun onStart() {
        super.onStart()
        getChildren()
    }

    private fun getChildren() {
        eftkadViewModel.getAllChildren(meetingId)
        lifecycleScope.launchWhenStarted {
            eftkadViewModel.stateFlowResponse.collect {
                when(it)
                {
                    is DataState.SuccessGetAllChildren ->{
                        setUpRecycleView(it.children as ArrayList<Child>)
                        children = it.children
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun setUpRecycleView(children: ArrayList<Child>)
    {
        eftkadAdapter = EftkadChildAdapter(this,children, this)
        children_in_meeting_eftkad_rv.setHasFixedSize(true)
        children_in_meeting_eftkad_rv.layoutManager = LinearLayoutManager(this)
        children_in_meeting_eftkad_rv.adapter = eftkadAdapter
    }

    override fun onChildEftkadListener(child: Child) {
        sendMessage(child)
    }

    private fun sendMessage(child: Child){
        val phoneTxt = "02${child.childPhone}"
        val msgText = "Hello"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$phoneTxt&text=$msgText")
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