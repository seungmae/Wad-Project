package com.wadapp.wad2.Share

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.wadapp.lsm.wad.R
import com.wadapp.lsm.wad.Share.CommentActivity
import com.wadapp.lsm.wad.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_popular.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.snippet_top_popularsharetoolbar.view.*

class PopularFragment : Fragment(){

    private val contentDTOs : ArrayList<ContentDTO> = ArrayList()
    private val contentUidList : ArrayList<String> = ArrayList()

    //파이어베이스
    var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null
    var imagesSnapshot : ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_popular, container, false)

        //파이어베이스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        view.popularwad.setOnClickListener {
            shareactivity_recycleview?.adapter?.notifyDataSetChanged()
        }

        return view
    }
    override fun onStart() {
        super.onStart()
        imagesSnapshot = firestore?.collection("posts")?.orderBy("favoriteCount", Query.Direction.DESCENDING)?.limit(15)?.addSnapshotListener {
                querySnapshot, _ ->
            contentDTOs.clear()
            contentUidList.clear()

            for(snapshot in querySnapshot!!.documents){
                var item = snapshot.toObject(ContentDTO :: class.java)

                contentDTOs.add(item!!)
                contentUidList.add(snapshot.id)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        //리사이클러뷰 적용
        shareactivity_recycleview.adapter = ShareRecyclerviewAdapter()
        shareactivity_recycleview.layoutManager = LinearLayoutManager(activity)

    }

    override fun onStop(){
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class ShareRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        init {
            swipe_Refresh.setOnRefreshListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    shareactivity_recycleview.adapter?.notifyDataSetChanged()
                    swipe_Refresh.isRefreshing = false
                },400)
            }
            swipe_Refresh.setColorSchemeResources(
                android.R.color.holo_green_light)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType : Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)

            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            //프로그레스
            val circularProgressDrawable = CircularProgressDrawable(context!!)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            val requestOptions = RequestOptions().placeholder(circularProgressDrawable)

            //유저 닉네임
            viewHolder.detalview_username_textview.text = contentDTOs[position].username

            //이미지
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(requestOptions).into(viewHolder.detailview_imageview_content)

            //설명텍스트
            viewHolder.detailview_explain_textview.text = contentDTOs[position].explain

            //오늘의단어
            viewHolder.detalview_word_textview.text = contentDTOs[position].todayword

            //좋아요 카운터
            viewHolder.detailview_favoritecounter_textview.text = "좋아요 " + contentDTOs[position].favoriteCount.toString() + "개"

            //댓글 카운터
            viewHolder.detailview_commentcounter_textview.text = "댓글 " + contentDTOs[position].commentCount.toString() + "개 보기"

            var uid = FirebaseAuth.getInstance().currentUser!!.uid

            //좋아요 클릭
            viewHolder.detailview_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
                viewHolder.detailview_favorite_imageview.setImageResource(R.drawable.ic_clover_green)

                if(contentDTOs[position].favorites.containsKey(uid)){
                    viewHolder.detailview_favoritecounter_textview.text = "좋아요 " + (contentDTOs[position].favoriteCount-1).toString() + "개"
                    viewHolder.detailview_favorite_imageview.setImageResource(R.drawable.ic_clover_white)
                }
                else{
                    viewHolder.detailview_favoritecounter_textview.text = "좋아요 " + (contentDTOs[position].favoriteCount+1).toString() + "개"
                }
            }

            //좋아요를 클릭한 상태
            if(contentDTOs[position].favorites.containsKey(uid)){
                viewHolder.detailview_favorite_imageview.setImageResource(R.drawable.ic_clover_green)
                //클릭하지 않은 상태
            }else{
                viewHolder.detailview_favorite_imageview.setImageResource(R.drawable.ic_clover_white)
            }

            //댓글보기 누르면
            viewHolder.detailview_commentcounter_textview.setOnClickListener {
                val intent = Intent(it.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }
        }

        //좋아요 이벤트 메소드
        private fun favoriteEvent(position : Int){
            var tsDoc = firestore?.collection("posts")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->
                var uid = FirebaseAuth.getInstance().currentUser!!.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                //좋아요를 누른 상태
                if(contentDTO!!.favorites.containsKey(uid)){
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)

                    //좋아요를 누르지 않은 상태
                }else{
                    contentDTO.favorites[uid] = true
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                }
                transaction.set(tsDoc, contentDTO)
            }
        }
    }
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}