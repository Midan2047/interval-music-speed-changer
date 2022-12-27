package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.ItemMusicBinding
import com.ddodang.intervalmusicspeedchanger.domain.model.Music

class MusicListAdapter(
    private val onClick: (Music) -> Unit,
    private val onDelete: (Music) -> Unit,
) : ListAdapter<Music, MusicListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem == newItem
        }
    }
) {

    class ViewHolder(
        private val binding: ItemMusicBinding,
        private val onClick: (Music) -> Unit,
        private val onDelete: (Music) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Music) {
            binding.textViewMusicTitle.text = item.title
            binding.textViewSingerMusicList.text = item.artist
            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.imageButtonRemove.setOnClickListener {
                onDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick,onDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}