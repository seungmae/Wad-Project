package com.wadapp.lsm.wad.Share

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.snippet_top_commenttoolbar.*
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_delete_comment.view.*


class CommentActivity : AppCompatActivity() {

    //파이어베이스
    var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null
    var contentUid : String? = null
    var commentSnapshot: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        //파이어베이스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        contentUid = intent.getStringExtra("contentUid")

        comment_btn_send.setOnClickListener {
            if(comment_edit_message.length() <= 0){
                Toast.makeText(this, "입력하세요", Toast.LENGTH_SHORT).show()
            }else{
                val comment = ContentDTO.Comment()

                val docRef : DocumentReference = firestore!!.collection("users").document(auth!!.currentUser!!.uid)

                commentevent()

                docRef.get().addOnCompleteListener {

                    var snapshot : DocumentSnapshot = it.result

                    comment.userId = FirebaseAuth.getInstance().currentUser!!.email
                    comment.comment = comment_edit_message.text.toString()
                    comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
                    comment.timestamp = System.currentTimeMillis()
                    comment.username = snapshot.getString("username") //닉네임 가져오기

                    FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(contentUid!!)
                        .collection("comments")
                        .document()
                        .set(comment)

                    comment_edit_message.setText("")

                }
            }
        }

        //뒤로가기
        ivBackArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        //edittext 라인수 제한
        comment_edit_message.addTextChangedListener(object : TextWatcher {
            var a = ""

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                a = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(comment_edit_message.lineCount >= 4){
                    comment_edit_message.setText(a)
                    comment_edit_message.setSelection(comment_edit_message.length())
                }
            }
        })

        //리사이클러뷰 적용
        comment_recyclerview.adapter = CommentRecyclerViewAdapter()
        comment_recyclerview.layoutManager = LinearLayoutManager(this)
    }

    //댓글 1증가 이벤트
    fun commentevent(){
        var tsDoc = firestore?.collection("posts")?.document(contentUid!!)
        firestore?.runTransaction { transaction ->
            var content = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            content?.commentCount = content?.commentCount!! + 1
            transaction.set(tsDoc,content)
        }
    }

    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val comments : ArrayList<ContentDTO.Comment> = ArrayList()

        init{
            commentSnapshot = FirebaseFirestore
                .getInstance()
                .collection("posts")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, _ ->
                    comments.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for(snapshot in querySnapshot.documents){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var comment  = comments[position]
            var view = holder.itemView
            var docRef : DocumentReference = firestore?.collection("posts")!!.document(contentUid!!)

            view.commentviewitem_textview_username.text = comments[position].username
            view.commentviewitem_textview_comment.text = comments[position].comment

            view.commentviewitem_textview_comment.setOnLongClickListener {

                docRef.collection("comments").get().addOnCompleteListener{

                    if(it.isSuccessful){
                        for( dc : DocumentSnapshot in it.result){
                            comment.documentid = dc.id
                        }
                        if(comment.uid == auth?.currentUser!!.uid){

                            //댓글삭제 다이어로그
                            var dialogview = LayoutInflater.from(this@CommentActivity).inflate(R.layout.dialog_delete_comment, null)
                            var builder : AlertDialog.Builder = AlertDialog.Builder(this@CommentActivity).setView(dialogview)

                            var dialog = builder.create()
                            //다이어로그 애니메이션
                            dialog.window.attributes.windowAnimations = R.style.animDialog
                            dialog.window.setBackgroundDrawableResource(R.drawable.dialog_style)
                            dialog.show()

                            dialogview.btn_deleteNo.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialogview.btn_deleteYes.setOnClickListener {
                                docRef.collection("comments").document(comment.documentid!!).delete()
                                dialog.dismiss()

                                //댓글 1감소 이벤트
                                firestore?.runTransaction {
                                    var content = it.get(docRef).toObject(ContentDTO::class.java)
                                    content!!.commentCount = content.commentCount - 1
                                    it.set(docRef,content)
                                }
                            }
                        }
                    }

                }
                true
            }
        }

    }

        private inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}


