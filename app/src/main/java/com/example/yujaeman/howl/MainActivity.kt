package com.example.yujaeman.howl

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
      when(p0.itemId)
      {
          R.id.action_home -> {
              var detailviewFragment = DetailviewFragment()
              supportFragmentManager.beginTransaction().replace(R.id.main_content,detailviewFragment).commit()
              return true
          }
          R.id.action_search -> {
              var gridFragment = GridFragment()
              supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
              return true
          }
          R.id.action_add_photo -> {
              if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                  startActivity(Intent(this, AddPhotoActivity::class.java))
              }
              return true
          }
          R.id.action_favorite_alarm-> {
              var alertFragment = AlertFragment()
              supportFragmentManager.beginTransaction().replace(R.id.main_content,alertFragment).commit()
              return true
          }
          R.id.action_account -> {
              var userFragment = UserFragment()
              supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
              return true
          }
      }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toolbar.bringToFront()
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

    }
}
