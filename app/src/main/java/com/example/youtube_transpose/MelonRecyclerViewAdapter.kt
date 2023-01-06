package com.example.youtube_transpose

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtube_transpose.databinding.HomeRecyclerItemBinding

class MelonRecyclerViewAdapter(dataList: MutableList<VideoData>): RecyclerView.Adapter<MelonRecyclerViewAdapter.MyViewHolder>() {
    private val dataList = dataList

    inner class MyViewHolder(private val binding: HomeRecyclerItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(melonData: VideoData){
            binding.accountTextView.text = melonData.account
            binding.titleTextView.text = melonData.title
            Glide.with(binding.thumbnailImageView)
                .load(melonData.thumbnail)
                .into(binding.thumbnailImageView)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HomeRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }
    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
}