package com.wadapp.lsm.wad.Settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.wadapp.lsm.wad.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_usernameset.*
import kotlinx.android.synthetic.main.snippet_top_usernamebar.*

class UsernamesetActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usernameset)

        progress_bar.visibility = View.GONE

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setup_username()

        ivBackArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        change_username.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            changeup_username()
        }
    }

    //닉네임 가져오기
    fun setup_username(){
        val docRef : DocumentReference = firestore!!.collection("users").document(auth!!.currentUser!!.uid)

        docRef?.get().addOnCompleteListener {
            var snapshot : DocumentSnapshot = it.result
            user_name_edit.setText(snapshot.getString("username"))

        }
    }

    //닉네임 변경
    fun changeup_username(){
        val docRef : DocumentReference = firestore!!.collection("users").document(auth!!.currentUser!!.uid)

        val items = HashMap<String, Any>()
        items.put("username", user_name_edit.text.toString())

        docRef.update(items).addOnSuccessListener {
            progress_bar.visibility = View.GONE
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            Toast.makeText(this, "완료", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
        }

    }
}