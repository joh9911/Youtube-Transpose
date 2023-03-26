package com.myFile.Transpose

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myFile.Transpose.databinding.HomePlaylistItemsRecyclerViewItemBinding

class PlaylistItemsRecyclerViewAdapter: ListAdapter<VideoData, PlaylistItemsRecyclerViewAdapter.MyViewHolder>(diffUtil) {

    inner class MyViewHolder(private val binding: HomePlaylistItemsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                itemClickListener.onClick(it, bindingAdapterPosition)
            }
            binding.optionButton.setOnClickListener {
                itemClickListener.optionButtonClick(it, bindingAdapterPosition)
            }

        }
        private fun selected(){
            binding.rankingTextView.setTextColor(Color.parseColor("#2196F3"))
            binding.channelTextView.setTextColor(Color.parseColor("#2196F3"))
            binding.titleTextView.setTextColor(Color.parseColor("#2196F3"))
        }
        private fun unSelected(){
            binding.rankingTextView.setTextColor(Color.parseColor("#FF000000"))
            binding.channelTextView.setTextColor(Color.parseColor("#898989"))
            binding.titleTextView.setTextColor(Color.parseColor("#FF000000"))
        }

        fun bind(videoData: VideoData, position: Int){
            binding.channelTextView.text = videoData.channelTitle
            binding.titleTextView.text = videoData.title
            binding.rankingTextView.text = (position + 1).toString()
            Glide.with(binding.thumbnailImageView)
                .load(videoData.thumbnail)
                .into(binding.thumbnailImageView)
            if (videoData.isPlaying)
                selected()
            else
                unSelected()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HomePlaylistItemsRecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return MyViewHolder(binding)
        }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(currentList[position], position)
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
        fun optionButtonClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener


    companion object diffUtil : DiffUtil.ItemCallback<VideoData>() {

        override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem == newItem
        }
    }

}