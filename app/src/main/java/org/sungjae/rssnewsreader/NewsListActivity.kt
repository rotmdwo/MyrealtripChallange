package org.sungjae.rssnewsreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import org.jsoup.Jsoup
import java.io.IOException

class NewsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)
        val mHandler = Handler()
        Thread(Runnable {
            var data = ""
            try {
                val doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").timeout(5000).get()
                val items = doc.select("rss channel item")
                for(item in items){
                    val title = item.select("title").text()
                    val link = item.select("link").text()
                    Log.d("asdf",link)
                    val docOfLink = Jsoup.connect(link).timeout(5000).get()
                    val description = docOfLink.select("head meta[property=og:description]").attr("content")
                    if(description.equals("")){
                        Log.d("asdf","2")
                    } else{
                        Log.d("asdf",description)
                    }

                }
            }catch (e : IOException){
                Log.d("asdf","1")
                e.printStackTrace()
            }
            mHandler.post{

            }
        }).start()
    }
}
