package org.sungjae.rssnewsreader

import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.jsoup.Jsoup  // 라이브러리: https://github.com/jhy/jsoup
import org.jsoup.select.Elements
import java.io.IOException

const val TIMEOUT_LIMIT = 5000
const val RSS_ADDRESS = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"
const val USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0"

fun getNewsInThread(mHandler: Handler, adapter: NewsAdapter, recyclerView: RecyclerView) { // 처음 로딩 시 뉴스리스트를 가져오는 메소드
    var items = Elements()
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

                        if (description.equals("")) { // meta[property=og:description] 태그 없으면 패스
                        } else {
                            val words = description.split(" ")
                            val wordsIterator = words.iterator()
                            val wordsLongerThan2Letters = ArrayList<String>()

                            while (wordsIterator.hasNext()) {  // 2글자 이상 단어만 선택, 공백으로 이루어진 단어 제외, '...'으로 이루어진 의미 없는 키워드 제외
                                val wordLongerThan2Letter = wordsIterator.next()
                                if (wordLongerThan2Letter.length >= 2 && !wordLongerThan2Letter.substring(0,2).equals("  ")) {
                                    if(wordLongerThan2Letter.length >= 3 && wordLongerThan2Letter.substring(0,3).equals("...")) continue
                                    wordsLongerThan2Letters.add(wordLongerThan2Letter)
                                }
                            }
                            val set = wordsLongerThan2Letters.toSet()  // 중복되는 글자 없게 세트로 거름

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
                            nonRepeatedWords.sortWith(kotlin.Comparator<Words> { a: Words, b: Words ->  // 키워드를 반복횟수 내림차순, 문자정렬 오름차순으로 정렬
                                when {
                                    a.numOfRepeats > b.numOfRepeats -> -1
                                    a.numOfRepeats < b.numOfRepeats -> 1
                                    else -> a.nonRepeatedWord.compareTo(b.nonRepeatedWord)
                                }
                            })

                            if (nonRepeatedWords.size >= 1 && !nonRepeatedWords[0].nonRepeatedWord.equals("")) keyWords[0] = nonRepeatedWords[0].nonRepeatedWord
                            if (nonRepeatedWords.size >= 2 && !nonRepeatedWords[1].nonRepeatedWord.equals("")) keyWords[1] = nonRepeatedWords[1].nonRepeatedWord
                            if (nonRepeatedWords.size >= 3 && !nonRepeatedWords[2].nonRepeatedWord.equals("")) keyWords[2] = nonRepeatedWords[2].nonRepeatedWord

                            adapter.addItem(News(title = title, description = description, keywords = keyWords, link = link, image = imageURL))
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

fun refreshNewsList(mHandler: Handler, adapter: NewsAdapter, recyclerView: RecyclerView, swipeLayout: SwipeRefreshLayout) { // 새로고침 시 뉴스리스트를 가져오는 메소드
    getNewsInThread(mHandler, adapter, recyclerView)
    recyclerView.removeAllViewsInLayout()
    adapter.deleteAllItem()
    swipeLayout.setRefreshing(false)
}