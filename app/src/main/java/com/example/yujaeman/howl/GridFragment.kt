package com.example.yujaeman.howl

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.request.RequestOptions
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore

class GridFragment : Fragment()
{
    var mainview : View?  = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mainview = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_grid,container,false)
        var recyclerView = mainview?.findViewById<RecyclerView>(R.id.gridfragment_recycleview)
        recyclerView?.adapter = GridFragmentRecyclerviewAdapter()
        recyclerView?.layoutManager = GridLayoutManager(activity,3)
        return  mainview
    }
    inner class GridFragmentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs : ArrayList<ContentDTO>

        init {
            contentDTOs = ArrayList()
            FirebaseFirestore.getInstance().collection("images").orderBy("timestamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                for(item in querySnapshot!!.documents)
                {
                    contentDTOs.add(item.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            var width = resources.displayMetrics.widthPixels/3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)

            return CustomViewHolder(imageView)
        }

        private inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView =  (holder as CustomViewHolder).imageView
            imageView.setImageResource(R.drawable.btn_signin_facebook)
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
            imageView.setOnClickListener {
                val fragment  = UserFragment()
                var bundle  = Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)
                bundle.putString("userId",contentDTOs[position].userId)

                fragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }
        }
    }

}