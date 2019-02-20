package com.wadapp.lsm.wad.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkCurrentUser()
        startLoading()
    }

    fun startLoading() {
        val handler = Handler()
        handler.postDelayed(Runnable { finish() }, 2000)
    }
    //로그인 되있는지 확인
    fun checkCurrentUser(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){ }
        else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
