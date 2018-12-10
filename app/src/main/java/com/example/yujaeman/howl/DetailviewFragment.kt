package com.example.yujaeman.howl

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailviewFragment : Fragment()
{
    var firestore : FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        firestore = FirebaseFirestore.getInstance()

        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail,container,false)
        view.detail_fragement_recyclerview.adapter = DetailRecyclerviewAdapter()
        view.detail_fragement_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view

    }

    inner class DetailRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs : ArrayList<ContentDTO>
        val contentUidList : ArrayList<String>


        init {

            contentDTOs = ArrayList()
            contentUidList = ArrayList()

            var uid = FirebaseAuth.getInstance().currentUser?.uid

            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents)
                {
                 var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged() // 새로 고침하는 코드
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

        }

        override fun getItemCount(): Int {
           return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewHolder = (holder as CustomViewHolder).itemView
            //유저 아이디
            viewHolder.detailviewitem_profile_text.text = contentDTOs!![position].userId
            //이미지
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_imageview_content)
            //설명 텍스트
            viewHolder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            // 좋아요 카운터 설정
            viewHolder.detailviewitem_favoritecounter_textview.text ="좋아요 " + contentDTOs!![position].favoriteCount +"개"


        }
    }
}