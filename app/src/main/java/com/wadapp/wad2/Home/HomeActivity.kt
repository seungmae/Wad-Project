package com.wadapp.lsm.wad.Home


import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Settings.SettingsActivity
import com.wadapp.lsm.wad.Share.PopularShareActivity
import com.wadapp.lsm.wad.Share.ShareActivity
import com.wadapp.lsm.wad.Share.UserShareActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wadapp.lsm.wad.BuildConfig
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.snippet_center_word.*
import kotlinx.android.synthetic.main.snippet_top_homebar.*
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        startActivity(Intent(this, SplashActivity::class.java))

        //최신버전 확인
        BaseApplication()
        checkGooglePlayServices()
        checkVersion()

        checkCurrentUser()
        todaywordrset()
        menubar()

        eventlottie()

        //오늘 단어 눌렀을때
        today_word.setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    //로티 (포인터이벤트)
    fun eventlottie() {
        val handler = Handler()

        handler.postDelayed(
            {
                lottie.visibility = View.GONE
            },
            5000)
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

    //오늘날짜 받기
    fun yearmonthday() : String{
        var fmt = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        var currentDate = fmt.format(Date())
        return currentDate
    }

    //오늘의 단어 초기화
    fun todaywordrset(){
        var db = FirebaseFirestore.getInstance()
        var docRef : DocumentReference = db.collection("wad-word").document(yearmonthday())
        docRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                var doc : DocumentSnapshot = task.result
                today_word.text = doc.getString("word")
            }else{
                today_word.text = "단어없음"
            }
        }
    }

    //메뉴바
    fun menubar(){
        val toggle = ActionBarDrawerToggle(Activity(), home_layout, toolbar,
            R.string.Open,
            R.string.Close
        )
        home_layout.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        todaywordrset()
    }

    //메뉴바 오버라이드
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> { }
            R.id.todayword -> {
                startActivity(Intent(this, ShareActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            R.id.userword -> {
                startActivity(Intent(this, UserShareActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            R.id.popular -> {
                startActivity(Intent(this, PopularShareActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }
        home_layout.closeDrawer(GravityCompat.START)
        return true
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
            Toast.makeText(this,"새로운 버전이 나왔습니다. 업데이트 해주세요!",Toast.LENGTH_LONG).show()
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
