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
                if(querySnapshot==null) return@addSnapshotListener // 데이터가 없는 경우 그냥 꺼지는 현상이 발생
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

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

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
            // 런 트랜젝션 기법을 이용 // 한명이 점유하고 있으면 다른 사용자는 사용 할 수 없게 하는 방식
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener { view->
                favoriteEvent(position)
            }
            //좋아요를 클릭했을 경우
            if(contentDTOs!![position].favorites.containsKey(uid))
            {
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }
            //좋아요를 클릭하지 않았을 경우
            else
            {
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
            viewHolder.detailviewitem_profile_image.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)
                bundle.putString("userId",contentDTOs[position].userId)
                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit()
            }
        }
        private fun favoriteEvent(position: Int)
        {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction {
                transaction ->
                var uid = FirebaseAuth.getInstance().currentUser!!.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                //좋아요를 누른 상태
                if(contentDTO!!.favorites.containsKey(uid)){
                    contentDTO.favoriteCount = contentDTO?.favoriteCount -1
                    contentDTO?.favorites.remove(uid)
                }
                // 좋아요를 누르지 않은 상태
                else
                {
                    contentDTO.favorites[uid] = true
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1

                }
                transaction.set(tsDoc,contentDTO)
            }
        }
    }

}