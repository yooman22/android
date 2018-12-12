package com.example.yujaeman.howl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_comment.*


class CommentActivity : AppCompatActivity() {

    var contentUid : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        commnet_recyclerview.adapter = CommentRecyclerviewAdapter()
        commnet_recyclerview.layoutManager = LinearLayoutManager(this)

        contentUid = intent.getStringExtra("contentUid")
        comment_btn_send.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.comment = comment_edit_message.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
        }
    }

    inner class CommentRecyclerviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment, parent, false)
            return CustomviewHolder(view)
        }

        inner class CustomviewHolder(view: View?) : RecyclerView.ViewHolder(view) // 메모리 누수를 막아주는 것

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        }

    }
}