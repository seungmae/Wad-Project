package com.wadapp.lsm.wad.Login

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.wadapp.lsm.wad.Home.HomeActivity
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setRegister()

        rgBackarrow.setOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
        }
    }

    private fun setRegister(){

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        progress_bar.visibility = View.GONE

        //가입하기 누르기
        btn_register.setOnClickListener {
            if (input_email.text.toString().isNullOrEmpty() || input_username.text.toString().isNullOrEmpty()
                || input_password.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{
                progress_bar.visibility = View.VISIBLE
                auth?.createUserWithEmailAndPassword(input_email.text.toString(),input_password.text.toString())
                    ?.addOnCompleteListener { task ->
                        if(!task.isSuccessful){
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                            progress_bar.visibility = View.GONE
                        }else if(task.isSuccessful){
                            progress_bar.visibility = View.GONE
                            setupuser()

                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        }
                    }
            }
        }
    }

    fun setupuser(){
        val UserDTO = UserDTO()

        UserDTO.uid = auth?.currentUser?.uid
        UserDTO.useremail = input_email.text.toString()
        UserDTO.username = input_username.text.toString()

        firestore?.collection("users")?.document(auth!!.currentUser!!.uid)?.set(UserDTO)

        setResult(Activity.RESULT_OK)

        finish()
    }
}
