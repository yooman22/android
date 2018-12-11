package com.example.yujaeman.howl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yujaeman.howl.model.ContentDTO
import com.example.yujaeman.howl.model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
class UserFragment : Fragment()
{
    var fragmentView : View? = null
    var PICK_FROFILE_FROM_ALBUM = 10
    var firestore : FirebaseFirestore? = null
    var currentUid: String? = null
    //현재 나의 uid
    var uid : String? = null
    //내가 선택한 uid
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentUid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("error","btn_check_before")
        if(arguments != null) {
            uid = arguments!!.getString("destinationUid")
        }
        firestore = FirebaseFirestore.getInstance()
        fragmentView = inflater.inflate(R.layout.fragment_user,container,false)
        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPcikerIntent = Intent(Intent.ACTION_PICK)
            photoPcikerIntent.type = "image/*"
            activity?.startActivityForResult(photoPcikerIntent,PICK_FROFILE_FROM_ALBUM)
        }
        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity!!,3) as RecyclerView.LayoutManager?
        getProfileImages()


            fragmentView?.account_btn_follow_signout?.setOnClickListener { view ->
                if(uid == null)
                {
                    Toast.makeText(this.context, "자신을 팔로우 할 수 없습니다.", Toast.LENGTH_LONG).show()
                }
                else
                {
                    requestFollow()
                }
        }

        return fragmentView
    }

    fun requestFollow()
    {
        var tsDocFollowing = firestore!!.collection("users").document(currentUid!!)

        Log.d("error","send message")

        firestore?.runTransaction {
                transaction ->
            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if(uid == currentUid)
            {
                return@runTransaction
            }
            if(followDTO == null)
            { // 아무도 팔로잉 하지 않았을 경우
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            }
            // 내 아이디가 제3자를 이미 팔로잉 하고 있을 경우
            if (followDTO.followers.containsKey(uid))
            {
                followDTO?.followerCount = followDTO?.followerCount -1
                followDTO?.followings.remove(uid)
            }
            else
            {
                // 내가 제3자를 팔로잉 하지 않았을 경우
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }
        Log.d("error","send message2")
        var tsDocFollower = firestore!!.collection("users").document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)

            if(followDTO == null)
            {
                //아무도 팔로워 하지 않았을 경우
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUid!!] = true
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }
            //제3자의 유저를 내가 팔로잉 하고 있을 경우
            if(followDTO!!.followers.containsKey(currentUid!!))
            {
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUid!!)

                transaction.set(tsDocFollower,followDTO!!)
            }
            else // 제3자를 내가 팔로워 하지 않았을 경우 -> 팔로워 하겠다.
            {
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUid!!] = true
            }

            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }

    }

    fun getProfileImages()
    {
        firestore?.collection("profileImages")?.document(currentUid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot?.data !=null)
            {
                var url = documentSnapshot?.data!!["image"]
                Glide.with(activity).load(url).apply(RequestOptions().circleCrop()).into(fragmentView!!.account_iv_profile)
            }
        }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
    {
        var contentDTOs : ArrayList<ContentDTO?>
        init {
            contentDTOs = ArrayList()
            firestore?.collection("images")?.whereEqualTo("uid",currentUid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for(snapshot in querySnapshot!!.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java))
                }
                account_tv_post_count.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageView  = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomviewHolder(imageView)
        }
        inner class CustomviewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
        override fun getItemCount(): Int {
           return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            var imageview = (holder as CustomviewHolder).imageView
            Glide.with(holder.itemView.context).load(contentDTOs[position]?.imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }


    }


}