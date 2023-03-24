package com.myFile.Transpose

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myFile.Transpose.databinding.ChannelVideoRecyclerViewHeaderViewBinding
import com.myFile.Transpose.databinding.ProgressBarItemBinding
import com.myFile.Transpose.databinding.SearchResultRecyclerItemBinding

class ChannelVideoRecyclerViewAdapter(channelData: ChannelData): ListAdapter<VideoData, RecyclerView.ViewHolder>(diffUtil) {
    private val VIEW_TYPE_HEADER = 0
//    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 1
    var channelDataVar = channelData


    inner class MyHeaderViewHolder(binding: ChannelVideoRecyclerViewHeaderViewBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.channelTitle.text = channelDataVar.channelTitle
            binding.channelInfo.text = "동영상 ${channelDataVar.channelVideoCount}개"
            binding.channelDescription.text = channelDataVar.channelDescription
            Glide.with(binding.channelBanner)
                .load(channelDataVar.channelBanner)
                .into(binding.channelBanner)
        }
    }
//    inner class MyProgressViewHolder(binding: ProgressBarItemBinding): RecyclerView.ViewHolder(binding.root){
//    }
    inner class MyViewHolder(private val binding: SearchResultRecyclerItemBinding): RecyclerView.ViewHolder(binding.root) {
        init{

            binding.thumbnailImageView.setOnClickListener {
                itemClickListener.videoClick(it, bindingAdapterPosition - 1)
            }
            binding.videoTitleChannelTitleLinearLayout.setOnClickListener {
                itemClickListener.videoClick(it, bindingAdapterPosition - 1)
            }
        }
        fun bind(videoData: VideoData){
            binding.channelTextView.text = videoData.channelTitle
            binding.titleTextView.text = videoData.title
            binding.videoDetailText.text = videoData.date
            Glide.with(binding.thumbnailImageView)
                .load(videoData.thumbnail)
                .into(binding.thumbnailImageView)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ChannelVideoRecyclerViewHeaderViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MyHeaderViewHolder(binding)
            }
            else -> {
                val binding = SearchResultRecyclerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MyViewHolder(binding)
            }
//            else -> {
//                val binding = ProgressBarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                MyProgressViewHolder(binding)
//            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MyViewHolder){
            holder.bind(currentList[position - 1])
        }


    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun channelClick(v: View, position: Int)
        fun videoClick(v: View, position: Int)
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