package com.example.yujaeman.howl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
class UserFragment : Fragment()
{
    var fragmentView : View? = null
    var PICK_FROFILE_FROM_ALBUM = 10
    var firestore : FirebaseFirestore? = null
    var uid: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore = FirebaseFirestore.getInstance()
        fragmentView = inflater.inflate(R.layout.fragment_user,container,false)
        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPcikerIntent = Intent(Intent.ACTION_PICK)
            photoPcikerIntent.type = "image/*"
            activity?.startActivityForResult(photoPcikerIntent,PICK_FROFILE_FROM_ALBUM)
        }
        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity!!,3)
        getProfileImages()
        return fragmentView
    }

    fun getProfileImages()
    {
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

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
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
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