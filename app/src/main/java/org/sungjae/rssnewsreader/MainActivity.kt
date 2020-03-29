package org.sungjae.rssnewsreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler()
        val delayedTime: Long = 1300
        val task = object : Runnable{
            override fun run(){
                val intent = Intent(applicationContext,NewsListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        handler.postDelayed(task,delayedTime)
    }
}
