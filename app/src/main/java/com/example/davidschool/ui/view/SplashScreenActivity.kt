package com.example.davidschool.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.davidschool.R
import com.example.davidschool.database.model.Khedma
import com.example.davidschool.ui.viewmodel.AddMeetingViewModel
import com.example.davidschool.utils.CommonMethod
import com.example.davidschool.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.flow.collect

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val splashTime = 4000
    private var topAnim: Animation? = null
    private var bottomAnim: Animation? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var meetings : ArrayList<Khedma> = ArrayList()
    private var lastMeetingId : Int = 0
    private val addMeetingViewModel: AddMeetingViewModel by viewModels()
    private lateinit var commonMethod: CommonMethod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.middle_anim)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim)

        sharedPreferences = this.getSharedPreferences("Splash", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        commonMethod = CommonMethod(this)




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            image_splash.animation = topAnim
            tittle_praise.animation = bottomAnim
        }

        Handler(Looper.getMainLooper()).postDelayed({
            navigateActivity(AllMeetingsActivity::class.java)

        }, splashTime.toLong())
    }


    private fun navigateActivity(activity : Class<*>)
    {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }


    private fun insertMeetingsIntoDatabase(meetings: ArrayList<Khedma>) {
        addMeetingViewModel.addMeeting(meetings)
        lifecycleScope.launchWhenStarted {
            addMeetingViewModel.stateFlowResponse.collect {
                when (it)
                {
                    is DataState.Loading -> {

                    }
                    is DataState.SuccessAddMeetings ->{
                        commonMethod.showMessage("Success")
                    }
                    is DataState.Failure -> {
                        commonMethod.showMessage(it.msg.toString())
                    }
                    else ->{

                    }
                }
            }
        }
    }


    private fun saveShared() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("First", true)
        editor.apply()
    }

    private fun checkShared(): Boolean {
        return sharedPreferences.getBoolean("First", false)
    }
}