package com.wadapp.wad2.Settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.wadapp.lsm.wad.Login.LoginActivity
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Settings.DeleteAccountActivity
import com.wadapp.lsm.wad.Settings.TermsActivity
import com.wadapp.lsm.wad.Settings.UsernamesetActivity
import com.wadapp.lsm.wad.Share.UserShareActivity
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.dialog_signout.view.*
import kotlinx.android.synthetic.main.item_settings.*
import kotlinx.android.synthetic.main.item_settings.view.*
import kotlinx.android.synthetic.main.snippet_top_settingbar.view.*

class SettingsFragment : Fragment(){

    private var auth : FirebaseAuth? = null
    private var firestore : FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_settings, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setup_username_email()

        view.settingwad.setOnClickListener {  }

        //닉네임
        view.user_name.setOnClickListener {
            startActivity(Intent(activity, UsernamesetActivity::class.java))
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //비밀번호
        view.user_password.setOnClickListener {
            changepasswordDialog()
        }

        view.user_share.setOnClickListener {
            startActivity(Intent(activity, UserShareActivity::class.java))
        }

        //이용약관
        view.info1.setOnClickListener {
            startActivity(Intent(activity, TermsActivity::class.java))
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //문의하기
        view.sendfeedback.setOnClickListener {
            sendFeedback()
        }

        //캐시삭제
        view.cachedelete.setOnClickListener {
            activity!!.cacheDir.deleteRecursively()
            Toast.makeText(context,"삭제 완료", Toast.LENGTH_SHORT).show()
        }

        //로그아웃
        view.logout.setOnClickListener {
            signoutDialog()
        }

        //계정탈퇴
        view.delete_account.setOnClickListener {
            startActivity(Intent(activity, DeleteAccountActivity::class.java))
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        return view
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

        var view : View = LayoutInflater.from(activity).inflate(R.layout.dialog_signout, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this!!.activity!!).setView(view)

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
            startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
        }

    }

    //비밀번호 변경 다이어로그
    fun changepasswordDialog(){

        var view : View = LayoutInflater.from(activity).inflate(R.layout.dialog_change_password, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this!!.activity!!).setView(view)

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
                    Toast.makeText(context,"메일을 보냈습니다", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(context,"메일 전송 실패", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    //메일 보내기
    fun sendFeedback() {
        var uri = Uri.parse("mailto:smwad7618@gmail.com" + "?cc=" + auth!!.currentUser!!.email!! + "&subject=" + "&body=" )
        var emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        emailIntent.putExtra(Intent.EXTRA_CC, uri)
        emailIntent.putExtra(Intent.EXTRA_BCC,uri)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[와드] 의견 보내기")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "의견 :")
        startActivity(Intent.createChooser(emailIntent, "이메일 선택"))
    }
}