package com.trototrackapp.trototrack.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.trototrackapp.trototrack.data.model.SliderData
import com.trototrackapp.trototrack.databinding.ItemSliderBinding

class SliderAdapter(private val items: List<SliderData>) : RecyclerView.Adapter<SliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: ItemSliderBinding) : RecyclerView.ViewHolder(itemView.root){
        private val binding = itemView
        fun bind(data: SliderData){
            with(binding){
                Glide.with(itemView)
                    .load(data.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemSliderBinding.inflate(LayoutInflater.from(parent.context),parent , false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position])
     }
}