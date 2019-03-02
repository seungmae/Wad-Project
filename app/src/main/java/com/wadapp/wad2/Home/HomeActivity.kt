package com.wadapp.lsm.wad.Home
import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.view.MenuItem
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Share.AddPhotoActivity
import com.wadapp.wad2.Home.HomeFragment
import com.wadapp.wad2.Settings.SettingsFragment
import com.wadapp.wad2.Share.PopularFragment
import com.wadapp.wad2.Share.ShareFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_share.*

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var idx = 0

    var monKeyBackPressedListener : onKeyBackPressedListener ?= null

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
       when(p0.itemId){
           R.id.home -> {
               idx = 0
               val homeFragment = HomeFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
               return true
           }
           R.id.todayword -> {
               idx = 1
               val shareFragment = ShareFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, shareFragment).commit()
               return true
           }
           R.id.share -> {
               startActivity(Intent(this, AddPhotoActivity::class.java))
               return true
           }
           R.id.popular -> {
               val popularFragment = PopularFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, popularFragment).commit()
               return true
           }
           R.id.settings -> {
               val settingsFragment = SettingsFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, settingsFragment).commit()
               return true
           }
       }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        startActivity(Intent(this, SplashActivity::class.java))
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        //안드로이드 사진 접근권한 허용
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)


    }

    //bottomnavigation 선택된 탭 체크
    fun setBottomTab(id : Int){
        bottom_navigation.menu.getItem(id).isChecked = true
    }

    //뒤로가기 리스너 생성
    interface onKeyBackPressedListener{
        fun onBackkey()
    }
    //뒤로가기 리스너 객체 생성
    fun setOnKeyBackPressedListener(listner: onKeyBackPressedListener?){
        monKeyBackPressedListener = listner
    }
    //뒤로가기 눌렀을 때
    override fun onBackPressed() {
        if(monKeyBackPressedListener != null){
            monKeyBackPressedListener!!.onBackkey()
        }else{
            super.onBackPressed()
        }
    }

}
