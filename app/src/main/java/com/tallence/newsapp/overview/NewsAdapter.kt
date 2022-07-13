package com.tallence.newsapp.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tallence.newsapp.databinding.ListItemNewsBinding
import com.tallence.newsapp.overview.model.Article

class NewsAdapter(private val onClickListener: NewsListener):
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    class NewsViewHolder(private var binding: ListItemNewsBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: NewsListener, article: Article) {
            binding.article = article
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NewsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemNewsBinding.inflate(layoutInflater, parent, false)

                return NewsViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.setOnClickListener {
            onClickListener.onClick(article)
        }
        holder.bind(onClickListener, article)
    }

    class NewsListener(val clickListener: (article: Article) -> Unit) {
        fun onClick(article: Article) = clickListener(article)
    }

    override fun getItemCount(): Int = differ.currentList.size

}