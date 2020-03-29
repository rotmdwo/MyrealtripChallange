package org.sungjae.rssnewsreader

import android.os.Handler
import android.util.Log
import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun crawllingTest(){
        val mHandler = Handler()
        val doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").get()
        val items = doc.select("rss channel item")
        var data = ""
        for(item in items){
            val link = item.select("link").text()
            data = link
        }
        assertEquals("https://news.google.com/",data.substring(0,24))
    }
}
