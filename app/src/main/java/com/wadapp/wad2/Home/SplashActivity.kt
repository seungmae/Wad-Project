package com.wadapp.lsm.wad.Home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wadapp.lsm.wad.BuildConfig
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import java.util.HashMap

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkCurrentUser()
        startLoading()

        //최신버전 확인
        BaseApplication()
        checkGooglePlayServices()
        checkVersion()
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
    //구글 플레이 서비스 설치 여부
    fun checkGooglePlayServices(){
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if(status != ConnectionResult.SUCCESS){
            val dialog = googleApiAvailability.getErrorDialog(this, status, -1)
            dialog.setOnDismissListener { finish() }
            dialog.show()

            googleApiAvailability.showErrorNotification(this, status)
        }
    }

    //현재 설치된 앱의 버전 리턴
    fun getAppVersion(context : Context) : String{
        var result = ""
        try{
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        }catch (e : PackageManager.NameNotFoundException){
            Log.e("getAppVersion", e.message)
        }
        return result
    }

    fun checkVersion(){
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val latestVersion = remoteConfig.getString("latest_version")
        val currentVersion = getAppVersion(this)

        if(!TextUtils.equals(currentVersion, latestVersion))
            Toast.makeText(this,"새로운 버전이 나왔습니다. 업데이트 해주세요!", Toast.LENGTH_LONG).show()
    }
}

class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        remoteConfigInit()
    }

    //Remote config 초기화
    fun remoteConfigInit(){
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG).build()

        val remoteConfigDefault = HashMap<String, Any>()
        remoteConfigDefault["latest_version"] = "1.0.0"

        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettings(configSettings)
            setDefaults(remoteConfigDefault)

            fetch(60).addOnCompleteListener { task: Task<Void> ->
                if(task.isSuccessful)
                    activateFetched()
            }
        }
    }
}
