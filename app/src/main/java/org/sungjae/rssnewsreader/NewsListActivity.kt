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
            try {
                val doc = Jsoup.connect("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko").timeout(5000).get()
                val items = doc.select("rss channel item")
                for(item in items){
                    val keyWords = Array<String>(3, init = { index -> "" })

                    val title = item.select("title").text()
                    val link = item.select("link").text()
                    val source = item.select("source").text()
                    if(source.equals("한국경제")){
                        Log.d("","SSLprotocolexception를 일으키는 한국경제 뉴스는 가져오지 않음")
                        continue
                    }
                    val docOfLink = Jsoup.connect(link).timeout(5000).ignoreHttpErrors(true).get()
                    val description = docOfLink.select("head meta[property=og:description]").attr("content")
                    val imageURL = docOfLink.select("head meta[property=og:image]").attr("content")

                    if(description.equals("")){ // meta[property=og:description] 태그 없으면
                        Log.d("asdf","2")
                    } else{
                        Log.d("asdf",description)
                        val words = description.split(" ")
                        val set = words.toSet()
                        //val nonRepeatedWords = Array<String>(set.size,init = { index -> "" })
                        //val numOfRepeats = Array<Int>(set.size,init = {index -> 0})

                        var nonRepeatedWords = Array<Words>(set.size,init={index -> Words("",0)})

                        val iterator = set.iterator()
                        var index = 0
                        while(iterator.hasNext()){
                            //nonRepeatedWords[index++] = iterator.next()
                            nonRepeatedWords[index++].nonRepeatedWord = iterator.next()
                        }
                        for(i in 0 until nonRepeatedWords.size){
                            for(j in 0 until words.size){
                                if(nonRepeatedWords[i].nonRepeatedWord.equals(words[j])){
                                    //numOfRepeats[i]++
                                    nonRepeatedWords[i].numOfRepeats++
                                }
                            }
                        }
                        nonRepeatedWords.sortWith(kotlin.Comparator<Words>{ a: Words, b:Words ->
                            when{
                                a.numOfRepeats > b.numOfRepeats -> -1
                                a.numOfRepeats < b.numOfRepeats -> 1
                                else -> a.nonRepeatedWord.compareTo(b.nonRepeatedWord)
                            }
                        })
                        if(!nonRepeatedWords[0].nonRepeatedWord.equals("")) keyWords[0] = nonRepeatedWords[0].nonRepeatedWord
                        if(!nonRepeatedWords[1].nonRepeatedWord.equals("")) keyWords[1] = nonRepeatedWords[1].nonRepeatedWord
                        if(!nonRepeatedWords[2].nonRepeatedWord.equals("")) keyWords[2] = nonRepeatedWords[2].nonRepeatedWord

                    }
                    if(imageURL.equals("")){ // meta[property=og:image] 태그 없으면
                        Log.d("asdf","3")
                    } else{
                        Log.d("asdf",imageURL)
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
