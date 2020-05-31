package com.ryspay.nurda.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ryspay.nurda.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity(val navNumber: Int): AppCompatActivity(){

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun setUpBottomNavigation(){
        bottom_navigation_view.setTextVisibility(false)
            .enableItemShiftingMode(false)
            .enableShiftingMode(false)
            .enableAnimation(false)
            .setIconSize(29f,29f)

        for(i in 0 until bottom_navigation_view.menu.size()){
            bottom_navigation_view.setIconTintList(i, null)
        }
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                when(it.itemId){
                    R.id.nav_item_home -> HomeActivity::class.java
                    R.id.nav_item_search -> SearchActivity::class.java
                    R.id.nav_item_share -> ShareActivity::class.java
                    R.id.nav_item_like -> LikesActivity::class.java
                    R.id.nav_item_profile -> ProfileActivity::class.java
                    else ->{
                        Log.d(TAG, "unknown nav item clicked")
                        null
                    }
                }
            if(nextActivity!=null){
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0,0) // 0's to turn off animation of next Activity
                true
            }else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(bottom_navigation_view!=null) {
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }

    companion object{
        const val TAG = "BaseActivity"
    }

}