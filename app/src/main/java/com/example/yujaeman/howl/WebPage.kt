package com.example.yujaeman.howl

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.yujaeman.howl.R.id.webview_link
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.webview.*

class WebPage : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)
        val url = "http://www.yujaeman.com/"
        this.webview_link.loadUrl(url)
        var messagingService = com.example.yujaeman.howl.MyFirebaseMessagingService()

    }

    class MyFirebaseMessagingService : FirebaseMessagingService() {

        private val TAG = "FirebaseService"

        /**
         * FirebaseInstanceIdService is deprecated.
         * this is new on firebase-messaging:17.1.0
         */
        override fun onNewToken(token: String?) {
            Log.d(TAG, "new Token: $token")
        }
    }



}
