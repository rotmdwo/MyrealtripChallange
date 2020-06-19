package org.sungjae.rssnewsreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

const val DELAYED_TIME: Long = 1300

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = NewsAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val mHandler = Handler()
        getNewsInThread(mHandler, adapter, recyclerView)  // 처음 뉴스리스트 로딩

        swipeLayout.setOnRefreshListener {  // 리프레쉬
            refreshNewsList(mHandler, adapter, recyclerView, swipeLayout)
        }

        val loadingHandler = Handler()
        val task = object : Runnable {  // 1.3초 후 로딩화면 사라짐
            override fun run(){
                newslogo.visibility = View.INVISIBLE
                rsslogo.visibility = View.INVISIBLE
                globelogo.visibility = View.INVISIBLE
                maintext.visibility = View.INVISIBLE
                versiontext.visibility = View.INVISIBLE

                swipeLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
            }
        }
        loadingHandler.postDelayed(task, DELAYED_TIME)
    }
}
