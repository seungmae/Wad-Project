package com.wadapp.wad2.Home

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.wadapp.lsm.wad.Home.HomeActivity
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Share.AddPhotoActivity
import com.wadapp.wad2.Share.ShareFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.snippet_center_word.*
import kotlinx.android.synthetic.main.snippet_center_word.view.*
import kotlinx.android.synthetic.main.snippet_top_homebar.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        todaywordrset()

        view.today_word.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ShareFragment()).commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as HomeActivity
        activity.setBottomTab(0)
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
                today_word.text = "WAD"
            }
        }
    }

}