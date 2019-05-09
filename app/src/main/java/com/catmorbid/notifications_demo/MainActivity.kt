package com.catmorbid.test_notifications

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catmorbid.notifications_demo.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_content)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, NotificationTester.newInstance())
                .commit();
    }

}