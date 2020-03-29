package org.sungjae.rssnewsreader

import android.os.Handler
import android.util.Log
import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException
import java.util.*

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
    fun stringOrderTest(){
        val ABC = "ABC"
        val BCD = "BCD"
        assertEquals(-1,ABC.compareTo(BCD))
    }

    @Test
    fun WordsSortTest(){
        var words = Array<Words>(3,init={index -> Words("",0)})
        words[0].nonRepeatedWord = "ABC"
        words[0].numOfRepeats = 2
        words[1].nonRepeatedWord = "GGG"
        words[1].numOfRepeats = 4
        words[2].nonRepeatedWord = "BBC"
        words[2].numOfRepeats = 2
        words.sortWith(kotlin.Comparator<Words>{ a: Words, b:Words ->
            when{
                a.numOfRepeats > b.numOfRepeats -> -1
                a.numOfRepeats < b.numOfRepeats -> 1
                else -> a.nonRepeatedWord.compareTo(b.nonRepeatedWord)
            }
        })
        assertEquals("GGG",words[0].nonRepeatedWord)
        assertEquals("ABC",words[1].nonRepeatedWord)
        assertEquals("BBC",words[2].nonRepeatedWord)
    }

    @Test
    fun crawlingTest(){
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
