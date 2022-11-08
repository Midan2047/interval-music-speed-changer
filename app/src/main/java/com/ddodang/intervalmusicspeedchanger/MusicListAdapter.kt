package com.ddodang.intervalmusicspeedchanger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ddodang.intervalmusicspeedchanger.databinding.ItemMusicBinding
import com.ddodang.intervalmusicspeedchanger.model.MusicInfo

class MusicListAdapter(
    private val onClick: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
) : ListAdapter<MusicInfo, MusicListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MusicInfo>() {
        override fun areItemsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            return oldItem == newItem
        }
    }
) {

    class ViewHolder(
        private val binding: ItemMusicBinding,
        private val onClick: (Int) -> Unit,
        private val onDelete: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MusicInfo) {
            binding.textViewMusicTitle.text = item.title
            binding.textViewSingerMusicList.text = item.artist
            binding.root.setOnClickListener {
                onClick(adapterPosition)
            }
            binding.imageButtonRemove.setOnClickListener {
                onDelete(adapterPosition)
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