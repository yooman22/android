package com.example.yujaeman.howl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yujaeman.howl.model.AlarmDTO
import com.example.yujaeman.howl.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentActivity : AppCompatActivity() {

    var contentUid : String? = null
    var user : FirebaseAuth? = null
    var destinationUid : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        user = FirebaseAuth.getInstance()
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")
        commnet_recyclerview.adapter = CommentRecyclerviewAdapter()
        commnet_recyclerview.layoutManager = LinearLayoutManager(this)


        comment_btn_send.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.comment = comment_edit_message.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            commentAlarm(destinationUid!!,comment_edit_message.text.toString())
            comment_edit_message.setText("")
        }
    }

    fun commentAlarm(desinationUid : String,message : String)
    {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = desinationUid
        alarmDTO.userId = user?.currentUser?.email
        alarmDTO.uid = user?.currentUser?.uid
        alarmDTO.kind = 1
        alarmDTO.message = message
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

    inner class CommentRecyclerviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments : ArrayList<ContentDTO.Comment>
        init {
            comments = ArrayList()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").orderBy("timestamp").addSnapshotListener {
                    querySnapshot, firebaseFirestoreException ->
                comments.clear()
                if(querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents!!)
                {
                    comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                }
                notifyDataSetChanged()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment, parent, false)
            return CustomviewHolder(view)
        }

        inner class CustomviewHolder(view: View?) : RecyclerView.ViewHolder(view) // 메모리 누수를 막아주는 것

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

            var view = holder!!.itemView
            view.commentviewitem_textview_comment.text = comments[position].comment
            view.commentviewitem_textview_profile.text = comments[position].userId
        }

    }
}