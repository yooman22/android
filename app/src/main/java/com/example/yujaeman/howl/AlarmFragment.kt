package com.example.yujaeman.howl

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yujaeman.howl.model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_comment.view.*
import java.util.zip.Inflater

class AlarmFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_alarm,container,false)
        var recyclerView = view.findViewById<RecyclerView>(R.id.alarmfragment_recycleview)
        recyclerView.adapter = AlarmRecyclerviewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }

    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
    {
        var alarmDTOList = ArrayList<AlarmDTO>()

        init {
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).orderBy("timestamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents!!)
                {
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment, parent, false)
            return CustomviewHolder(view)
        }
        inner class CustomviewHolder(view: View?) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            var commentTextview = holder?.itemView?.commentviewitem_textview_profile

            when(alarmDTOList[position].kind)
            {
                0->{
                    var str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                    commentTextview?.text = str_0
                }
                1->{
                    var str_1 = alarmDTOList[position].userId + getString(R.string.alarm_who) + " " +
                            alarmDTOList[position].message + " " +
                            getString(R.string.alarm_comment)
                    commentTextview?.text = str_1
                }
                2->{
                    var str_2 = alarmDTOList[position].userId + getString(R.string.alarm_follow)
                    commentTextview?.text = str_2
                }
            }

        }

    }
}