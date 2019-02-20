package com.wadapp.lsm.wad.Share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.dialog_signout.view.*
import kotlinx.android.synthetic.main.snippet_top_addphoto_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private val PICK_IMAGE_FROM_ALBUM = 0
    private var storage : FirebaseStorage? = null
    private var photoUri : Uri? = null

    private var auth : FirebaseAuth? = null
    private var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        progress_bar.visibility = View.GONE

        storage = FirebaseStorage.getInstance()

        //파이어베이스 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        openalbum()

        //공유하기 눌렀을때
        photo_share.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            contentUpload()
        }

        //취소 눌렀을때
        photo_cancel.setOnClickListener {
            addphotocancelDialog()
        }

        //edittext 라인수 제한
        addphoto_edit_explain.addTextChangedListener(object : TextWatcher{
            var sample = ""

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                sample = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(addphoto_edit_explain.lineCount >= 8){
                    addphoto_edit_explain.setText(sample)
                    addphoto_edit_explain.setSelection(addphoto_edit_explain.length())
                }
            }
        })

    }

    private fun openalbum(){
        //사진 앨범 오픈
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener {
            //이미지 눌렀을때도 오픈
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Glide.with(this).load(photoUri).into(addphoto_image)
            }else{
                finish()
            }
        }
    }

    //오늘날짜 받기
    fun yearmonthday() : String{
        var fmt  = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        var currentDate = fmt.format(Date())
        return currentDate
    }

    //사진 업로드
    private fun contentUpload(){
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss",Locale.KOREA).format(Date())
        val imageFileName = "png_" + timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        val db = FirebaseFirestore.getInstance()


        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {

            //업로드된 이미지 주소
            storageRef.downloadUrl.addOnCompleteListener {

                val docRef : DocumentReference = db.collection("users").document(auth!!.currentUser!!.uid)

                docRef.get().addOnCompleteListener { task ->
                    progress_bar.visibility = View.GONE
                    Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()

                    var uri = it.result.toString()
                    val contentDTO = ContentDTO()
                    var snapshot : DocumentSnapshot = task.result

                    var docRef : DocumentReference = db.collection("wad-word").document(yearmonthday())
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var doc: DocumentSnapshot = task.result
                            //이미지 주소
                            contentDTO.imageUrl = uri

                            //유저의 UID
                            contentDTO.uid = auth?.currentUser?.uid

                            //유저의 닉네임
                            contentDTO.username = snapshot.getString("username")

                            //오늘의 단어
                            contentDTO.todayword = doc.getString("word")

                            //게시물 설명
                            contentDTO.explain = addphoto_edit_explain.text.toString()

                            //유저 아이디
                            contentDTO.userId = auth?.currentUser?.email

                            //게시물 업로드 시간
                            contentDTO.timestamp = System.currentTimeMillis()

                            firestore?.collection("posts")?.document(timeStamp)?.set(contentDTO)

                            setResult(Activity.RESULT_OK)

                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun addphotocancelDialog(){

        var view : View = LayoutInflater.from(this).inflate(R.layout.dialog_addphoto_cancel, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this).setView(view)

        var dialog = builder.create()
        //다이어로그 애니메이션
        dialog.window.attributes.windowAnimations = R.style.animDialog
        dialog.window.setBackgroundDrawableResource(R.drawable.dialog_style)
        dialog.show()

        view.btn_SignoutNo.setOnClickListener {
            dialog.dismiss()
        }
        view.btn_SignoutYes.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

    }
}
