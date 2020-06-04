package com.ryspay.nurda.views

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.ryspay.nurda.R
import com.ryspay.nurda.screens.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*

class InstagramBottomNavigation(private val bnv: BottomNavigationViewEx,
                                private val navNumber: Int, activity: Activity): LifecycleObserver{
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        bnv.menu.getItem(navNumber).isChecked = true
    }

    init {
       bnv.setTextVisibility(false)
            .enableItemShiftingMode(false)
            .enableShiftingMode(false)
            .enableAnimation(false)
            .setIconSize(29f,29f)

        for(i in 0 until bnv.menu.size()){
           bnv.setIconTintList(i, null)
        }
       bnv.setOnNavigationItemSelectedListener {
            val nextActivity =
                when(it.itemId){
                    R.id.nav_item_home -> HomeActivity::class.java
                    R.id.nav_item_search -> SearchActivity::class.java
                    R.id.nav_item_share -> ShareActivity::class.java
                    R.id.nav_item_like -> LikesActivity::class.java
                    R.id.nav_item_profile -> ProfileActivity::class.java
                    else ->{
                        Log.d(BaseActivity.TAG, "unknown nav item clicked")
                        null
                    }
                }
            if(nextActivity!=null){
                val intent = Intent(activity , nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                activity.overridePendingTransition(0,0) // 0's to turn off animation of next Activity
                true
            }else {
                false
            }
        }
    }
}

fun BaseActivity.setUpBottomNavigation(navNumber: Int){
    InstagramBottomNavigation(bottom_navigation_view, navNumber,this)
}