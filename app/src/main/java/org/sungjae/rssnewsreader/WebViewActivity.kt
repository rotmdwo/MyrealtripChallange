package org.sungjae.rssnewsreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val intent = getIntent()
        val link = intent.extras.getString("link")
        title1.text = intent.extras.getString("title")
        val keyWords = intent.extras.getStringArray("keywords")
        keyword1.text = keyWords[0]
        keyword2.text = keyWords[1]
        keyword3.text = keyWords[2]
        webView.setWebViewClient(WebViewClient())
        webView.loadUrl(link)

        backButton.setOnClickListener{
            finish()
        }
    }
}
