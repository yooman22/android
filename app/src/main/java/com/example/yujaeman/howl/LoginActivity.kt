package com.example.yujaeman.howl

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo
import android.content.pm.PackageInfo
import android.nfc.Tag
import android.text.method.MovementMethod
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.security.MessageDigest
import java.util.*
import javax.security.auth.callback.Callback


class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        auth = FirebaseAuth.getInstance()

        email_login_button.setOnClickListener{ view ->
            createAndLoginEmail()
        }
        google_sign_in_button.setOnClickListener { view->
            googleLogin()
        }
        facebook_login_button.setOnClickListener { view->
            facebookLogin()
        }
        twitter_sign_in_button.setOnClickListener { view ->
            //twitterLogin()
        }



        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        callbackManager = CallbackManager.Factory.create()

        //printHashKey(this)
    }
//
//    fun printHashKey(context: Context) {
//        try {
//            val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.i("AppLog", "key:$hashKey=")
//            }
//        } catch (e: Exception) {
//            Log.e("AppLog", "error:", e)
//        }
//
//    }

    fun createAndLoginEmail()
    {
        if(email_edittext.text.toString() == "")
        {
            Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_LONG).show()
        }
        else if(password_edittext.text.toString() == "")
        {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
        }
        else {
            auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "아이디 생성 성공", Toast.LENGTH_LONG).show()
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        Log.d("error", "login error2")
                        Toast.makeText(this, "아이디 생성 실패", Toast.LENGTH_LONG).show()
                    } else {
                        Log.d("error", "login erro3")
                        emailLogin()
                    }
                }
        }
    }

    fun emailLogin()
    {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                moveMainPage(auth?.currentUser)
                //Toast.makeText(this,"로그인이 성공했습니다.",Toast.LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(this,"로그인 실패",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage(user : FirebaseUser?)
    {
        if(user !=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            this.finish()
        }
    }

    fun googleLogin()
    {
        var signIntent = googleSignInClient?.signInIntent
        startActivityForResult(signIntent,GOOGLE_LOGIN_CODE)
    }


    fun firebaseAuthWithGoogle(account : GoogleSignInAccount)
    {
        var credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)
        moveMainPage(auth?.currentUser)
        this.finish()
    }

    fun facebookLogin()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
        LoginManager.getInstance().registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFaacebookAccessToken(result?.accessToken)

            }

            override fun onCancel() {


            }

            override fun onError(error: FacebookException?) {
                Log.d("error",error.toString())
            }

        })
    }

    fun handleFaacebookAccessToken(token:AccessToken?)
    {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)

        auth?.signInWithCredential(credential)?.addOnCompleteListener{task->
            if(task.isSuccessful)
            {
                moveMainPage(auth?.currentUser)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        moveMainPage(auth?.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)


        if(GOOGLE_LOGIN_CODE == requestCode) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                var account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            }
            else
            {
                var message : String = "구글 연결에 실패하였습니다"
                Toast.makeText(applicationContext,message,message.length)
            }
        }
    }




}
