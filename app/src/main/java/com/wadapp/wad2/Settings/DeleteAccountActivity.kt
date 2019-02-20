package com.wadapp.lsm.wad.Settings


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_deleteaccount.*
import kotlinx.android.synthetic.main.dialog_deleteaccount.view.*
import kotlinx.android.synthetic.main.snippet_top_deleteaccountbar.*

class DeleteAccountActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleteaccount)

        progress_bar.visibility = View.GONE

        ivBackArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        checkuser()

    }

    fun checkuser(){
        auth = FirebaseAuth.getInstance()
        delete_user.setOnClickListener {

            if(input_password.text.toString().isNullOrEmpty()){
                Toast.makeText(this,"비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{
                progress_bar.visibility = View.VISIBLE
                auth?.signInWithEmailAndPassword(auth!!.currentUser!!.email!!, input_password.text.toString())
                    ?.addOnCompleteListener {task ->
                        val user = FirebaseAuth.getInstance().currentUser
                        val emailVerified = user?.isEmailVerified

                        if(!task.isSuccessful){
                            progress_bar.visibility = View.GONE
                            Toast.makeText(this,"비밀번호가 틀립니다.",Toast.LENGTH_SHORT).show()
                        }else{
                            if(emailVerified == false){
                                progress_bar.visibility = View.GONE
                                DeleteaccountDialog()
                            }
                            else{
                                progress_bar.visibility = View.GONE
                                Toast.makeText(this, "이메일 확인바람", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }

    }

    fun DeleteaccountDialog(){

        var view : View = LayoutInflater.from(this).inflate(R.layout.dialog_deleteaccount, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this).setView(view)

        var dialog = builder.create()
        //다이어로그 애니메이션
        dialog!!.window.attributes.windowAnimations = R.style.animDialog
        dialog.window.setBackgroundDrawableResource(R.drawable.dialog_style)
        dialog.show()

        view.btn_SignoutNo.setOnClickListener {
            dialog.dismiss()
        }
        view.btn_SignoutYes.setOnClickListener {
            deletedatabase()

            var user = FirebaseAuth.getInstance().currentUser
            user!!.delete().addOnCompleteListener {
                if(it.isSuccessful){
                    auth?.signOut()
                    Toast.makeText(this, "탈퇴 완료", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "탈퇴 오류", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    fun deletedatabase(){
        var user = FirebaseAuth.getInstance().currentUser?.uid
        var db = FirebaseFirestore.getInstance()
        var docRef : DocumentReference = db.collection("users").document(user!!)

        docRef.delete().addOnSuccessListener {void -> }

    }

}