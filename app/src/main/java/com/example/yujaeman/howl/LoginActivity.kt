package com.example.yujaeman.howl

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

class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_login_button.setOnClickListener { view ->
            createAndLoginEmail()
        }

        google_sign_in_button.setOnClickListener { view->
            googleLogin()
        }



        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

    }

    override fun onDestroy() {
        super.onDestroy()
       FirebaseAuth.getInstance().signOut()
    }

    fun createAndLoginEmail()
    {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener{
                task -> if(task.isSuccessful){
            Toast.makeText(this,"아이디 생성 성공",Toast.LENGTH_LONG).show()
        }
        else if(task.exception?.message.isNullOrEmpty())
        {
            Toast.makeText(this,"아이디 생성 실패",Toast.LENGTH_LONG).show()
        }
        else
        {
            emailLogin()
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
        startActivity(Intent(this,MainActivity::class.java))
        this.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
