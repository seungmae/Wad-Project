package com.wadapp.lsm.wad.Settings

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_change_password.view.*

import kotlinx.android.synthetic.main.dialog_signout.view.*
import kotlinx.android.synthetic.main.item_settings.*
import kotlinx.android.synthetic.main.snippet_top_settingbar.*

class SettingsActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setup_username_email()

        //닉네임
        user_name.setOnClickListener {
            startActivity(Intent(this, UsernamesetActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //비밀번호
        user_password.setOnClickListener {
            changepasswordDialog()
        }

        //이용약관
        info1.setOnClickListener {
            startActivity(Intent(this, TermsActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //문의하기
        sendfeedback.setOnClickListener {
            sendFeedback()
        }

        //캐시삭제
        cachedelete.setOnClickListener {
            this.cacheDir.deleteRecursively()
            Toast.makeText(this,"삭제 완료", Toast.LENGTH_SHORT).show()
        }

        //로그아웃
        logout.setOnClickListener {
            signoutDialog()
        }

        //계정탈퇴
        delete_account.setOnClickListener {
            startActivity(Intent(this, DeleteAccountActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //뒤로가기
        ivBackArrow.setOnClickListener {
            finish()
            //왼쪽에서 오른쪽으로 화면 애니메이션
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }


    }

    override fun onResume() {
        super.onResume()
        setup_username_email()
    }

    //닉네임, 이메일 set
    fun setup_username_email(){

        val docRef : DocumentReference = firestore!!.collection("users").document(auth!!.currentUser!!.uid)

        docRef.get().addOnCompleteListener {
            var snapshot : DocumentSnapshot = it.result
            text_user_name.text = snapshot.getString("username")
            text_user_email.text = auth?.currentUser?.email

        }
    }

    //로그아웃 다이어로그
    fun signoutDialog(){

        var view : View = LayoutInflater.from(this).inflate(R.layout.dialog_signout, null)
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
            auth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    //비밀번호 변경 다이어로그
    fun changepasswordDialog(){

        var view : View = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this).setView(view)

        var dialog = builder.create()
        //다이어로그 애니메이션
        dialog.window.attributes.windowAnimations = R.style.animDialog
        dialog.window.setBackgroundDrawableResource(R.drawable.dialog_style)
        dialog.show()

        view.btn_emailNo.setOnClickListener {
            dialog.dismiss()
        }
        view.btn_emailYes.setOnClickListener {

            auth!!.sendPasswordResetEmail(auth!!.currentUser!!.email!!).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"메일을 보냈습니다", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            else{
                Toast.makeText(this,"메일 전송 실패", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
             }
            }
        }
    }

    //메일 보내기
    fun sendFeedback() {
        var uri = Uri.parse("mailto:smwad7618@gmail.com" + "?cc=" + auth!!.currentUser!!.email!! + "&subject=" + "&body=" )
        var emailIntent = Intent(ACTION_SENDTO, uri)
        emailIntent.putExtra(Intent.EXTRA_CC, uri)
        emailIntent.putExtra(Intent.EXTRA_BCC,uri)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[와드] 의견 보내기")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "의견 :")
        startActivity(Intent.createChooser(emailIntent, "이메일 선택"))
    }

}