package com.wadapp.lsm.wad.Home
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Share.AddPhotoActivity
import com.wadapp.wad2.Home.HomeFragment
import com.wadapp.wad2.Settings.SettingsFragment
import com.wadapp.wad2.Share.PopularFragment
import com.wadapp.wad2.Share.ShareFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
       when(p0.itemId){
           R.id.home -> {
               val homeFragment = HomeFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
               return true
           }
           R.id.todayword -> {
               val shareFragment = ShareFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, shareFragment).commit()
               return true
           }
           R.id.share -> {
               startActivity(Intent(this, AddPhotoActivity::class.java))
               return true
           }
           R.id.popular -> {
               val PopularFragment = PopularFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PopularFragment).commit()
               return true
           }
           R.id.settings -> {
               val SettingsFragment = SettingsFragment()
               supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment).commit()
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
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }
}
