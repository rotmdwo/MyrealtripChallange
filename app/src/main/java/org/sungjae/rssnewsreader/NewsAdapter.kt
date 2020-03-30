package org.sungjae.rssnewsreader

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // 라이브러리: https://github.com/bumptech/glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.newsitem.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    val mContext: Context
    private val items = ArrayList<News>()

    constructor(context: Context) {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.newsitem,parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        val item = items.get(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(item: News) {
        items.add(item)
    }

    fun deleteAllItem() {
        while (items.size > 1) {
            items.removeAt(0)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.title
        val content = itemView.content
        val keyword1 = itemView.keyword1
        val keyword2 = itemView.keyword2
        val keyword3 = itemView.keyword3
        val imageView = itemView.imageView

        fun bind(item: News) {
            title.text = item.title
            content.text = item.description
            keyword1.text = item.keywords[0]
            keyword2.text = item.keywords[1]
            keyword3.text = item.keywords[2]

            val options = RequestOptions().error(R.drawable.noimage)
            Glide.with(mContext)
                .load(item.image)
                .apply(options)
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(mContext, WebViewActivity::class.java)
                intent.putExtra("link", item.link)
                intent.putExtra("title", item.title)
                intent.putExtra("keywords", item.keywords)
                mContext.startActivity(intent)
            }
        }
    }
}