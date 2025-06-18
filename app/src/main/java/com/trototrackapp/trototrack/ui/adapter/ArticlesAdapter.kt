package com.trototrackapp.trototrack.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trototrackapp.trototrack.data.remote.response.DataArticle
import com.trototrackapp.trototrack.databinding.ItemArticlesBinding
import com.trototrackapp.trototrack.ui.detail.DetailArticleActivity

class ArticlesAdapter : ListAdapter<DataArticle, ArticlesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class MyViewHolder(private val binding: ItemArticlesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: DataArticle){
            binding.articleTitle.text = article.title
            binding.articleDescription.text = article.description
            Glide.with(binding.root)
                .load(article.image)
                .into(binding.articleImage)
                .clearOnDetach()
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailArticleActivity::class.java).apply {
                    putExtra("title", article.title)
                    putExtra("description", article.description)
                    putExtra("image", article.image)
                    putExtra("date", article.createdAt)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataArticle>() {
            override fun areItemsTheSame(oldItem: DataArticle, newItem: DataArticle): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataArticle, newItem: DataArticle): Boolean {
                return oldItem == newItem
            }
        }
    }
}