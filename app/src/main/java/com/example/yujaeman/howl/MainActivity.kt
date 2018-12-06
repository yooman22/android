package com.example.yujaeman.howl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogOut_Button.setOnClickListener {
            view ->
            FirebaseAuth.getInstance().signOut()

            var message : String = "Logout 성공"
            Toast.makeText(this,message,message.length)

        }

    }




}
