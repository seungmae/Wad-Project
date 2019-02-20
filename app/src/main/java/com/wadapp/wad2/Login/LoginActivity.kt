package com.wadapp.lsm.wad.Login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.wadapp.lsm.wad.Home.HomeActivity
import com.wadapp.lsm.wad.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progress_bar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        Logininit()
    }
    fun Logininit(){

        auth = FirebaseAuth.getInstance()

        //로그인 누르기
        btn_login.setOnClickListener {
            if(input_email.text.toString().isNullOrEmpty() || input_password.text.toString().isNullOrEmpty()){
                Toast.makeText(this,"모두 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{
                progress_bar.visibility = View.VISIBLE
                auth?.signInWithEmailAndPassword(input_email.text.toString(),input_password.text.toString())
                    ?.addOnCompleteListener { task ->
                        val user = FirebaseAuth.getInstance().currentUser
                        val emailVerified  = user?.isEmailVerified

                        if(!task.isSuccessful){
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                            progress_bar.visibility = View.GONE
                        }else {

                           if(emailVerified == false){
                               startActivity(Intent(this, HomeActivity::class.java))
                               finish()
                               overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

                           }
                           else{
                               Toast.makeText(this, "이메일 확인바람", Toast.LENGTH_SHORT).show()
                               progress_bar.visibility = View.GONE
                               auth?.signOut()
                           }
                        }
                    }
            }
        }

        link_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(
                R.anim.slide_from_right,
                R.anim.slide_to_left
            )
        }
    }

}
