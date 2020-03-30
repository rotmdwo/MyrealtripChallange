package org.sungjae.rssnewsreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_news_list.*
import org.jsoup.Jsoup // 라이브러리: https://github.com/jhy/jsoup
import org.jsoup.select.Elements
import java.io.IOException

const val TIMEOUT_LIMIT = 5000
const val RSS_ADDRESS = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"
const val USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0"

class NewsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        val adapter = NewsAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val mHandler = Handler()
        getNewsInThread(mHandler, adapter)

        swipeLayout.setOnRefreshListener {
            recyclerView.removeAllViewsInLayout()
            adapter.deleteAllItem()
            getNewsInThread(mHandler, adapter)
            swipeLayout.setRefreshing(false)
        }
    }

    private fun getNewsInThread(mHandler: Handler, adapter: NewsAdapter) {
        var items: Elements = Elements()
        Thread(Runnable {

            try {
                val doc = Jsoup.connect(RSS_ADDRESS)
                    .timeout(TIMEOUT_LIMIT)
                    .get()
                val tempItems = doc.select("rss channel item")
                items = tempItems
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mHandler.post {
                for (item in items) {
                    Thread(Runnable {
                        try {
                            val keyWords = Array<String>(3, init = { index -> "" })

                            val title = item.select("title").text()
                            val link = item.select("link").text()

                            val docOfLink = Jsoup.connect(link)
                                .timeout(TIMEOUT_LIMIT)
                                .userAgent(USER_AGENT)
                                .ignoreHttpErrors(true)
                                .get()
                            val description = docOfLink
                                .select("head meta[property=og:description]")
                                .attr("content")
                            val imageURL = docOfLink
                                .select("head meta[property=og:image]")
                                .attr("content")

                            if (description.equals("")) { // meta[property=og:description] 태그 없으면
                            } else {
                                val words = description.split(" ")
                                val wordsIterator = words.iterator()
                                val wordsLongerThan2Letters = ArrayList<String>()

                                while (wordsIterator.hasNext()) {
                                    val wordLongerThan2Letter = wordsIterator.next()
                                    if(wordLongerThan2Letter.length>=2) {
                                        wordsLongerThan2Letters.add(wordLongerThan2Letter)
                                    }
                                }
                                val set = wordsLongerThan2Letters.toSet()

                                var nonRepeatedWords = Array<Words>(set.size, init = {index -> Words("",0)})

                                val iterator = set.iterator()
                                var index = 0
                                while (iterator.hasNext()) {
                                    nonRepeatedWords[index++].nonRepeatedWord = iterator.next()
                                }
                                for (i in 0 until nonRepeatedWords.size) {
                                    for (j in 0 until words.size) {
                                        if (nonRepeatedWords[i].nonRepeatedWord.equals(words[j])) {
                                            nonRepeatedWords[i].numOfRepeats++
                                        }
                                    }
                                }
                                nonRepeatedWords.sortWith(kotlin.Comparator<Words> { a: Words, b: Words ->
                                    when {
                                        a.numOfRepeats > b.numOfRepeats -> -1
                                        a.numOfRepeats < b.numOfRepeats -> 1
                                        else -> a.nonRepeatedWord.compareTo(b.nonRepeatedWord)
                                    }
                                })

                                if (!nonRepeatedWords[0].nonRepeatedWord.equals("")) keyWords[0] = nonRepeatedWords[0].nonRepeatedWord
                                if (!nonRepeatedWords[1].nonRepeatedWord.equals("")) keyWords[1] = nonRepeatedWords[1].nonRepeatedWord
                                if (!nonRepeatedWords[2].nonRepeatedWord.equals("")) keyWords[2] = nonRepeatedWords[2].nonRepeatedWord

                                if (imageURL.equals("")) { // meta[property=og:image] 태그 없으면
                                } else {
                                    adapter.addItem(News(title = title,description = description,keywords = keyWords,link = link,image = imageURL))
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        mHandler.post {
                            recyclerView.adapter = adapter
                        }
                    }).start()
                }
            }
        }).start()


    }
}
