package com.myFile.Transpose

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.myFile.Transpose.databinding.FragmentPlayerBinding
import com.myFile.Transpose.databinding.MainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.myFile.Transpose.model.ChannelSearchData
import com.myFile.Transpose.model.HeaderViewData
import com.myFile.Transpose.model.RelatedVideoData
import com.myFile.Transpose.model.VideoDetailData
import kotlinx.coroutines.*
import java.lang.Math.abs
import kotlin.collections.ArrayList


class PlayerFragment(private val videoData: VideoData, private val currentFragment: Fragment): Fragment() {
    lateinit var mainBinding: MainBinding
    lateinit var activity: Activity
    lateinit var relatedVideoRecyclerViewAdapter: RelatedVideoRecyclerViewAdapter
    lateinit var playlistItemsRecyclerViewAdapter: PlaylistItemsRecyclerViewAdapter
    var fbinding: FragmentPlayerBinding? = null
    val binding get() = fbinding!!
//    var playerModel = PlayerModel(playMusicList = videoDataList,
//    currentPosition = position)

    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler
    var nextPageToken = ""


    private val relatedVideoList = ArrayList<VideoData>()
    private lateinit var currentVideoDetailData: VideoDetailData
    private lateinit var currentChannelSearchData: ChannelSearchData

    var player: ExoPlayer? = null
    lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fbinding = FragmentPlayerBinding.inflate(inflater, container, false)
        mainBinding = MainBinding.inflate(layoutInflater)
        activity.videoService!!.initPlayerFragment(this)
        val view = binding.root
        initRecyclerView()
        initExceptionHandler()
        getDetailData(videoData.videoId)
        initView()
        initMotionLayout()
        initListener()
        return view
    }
    private fun initExceptionHandler(){
        coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
            Log.d("코루틴 에러","$throwable")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(activity,R.string.network_error_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getDetailData(videoId: String) {
        relatedVideoList.clear()
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            async {getVideoDetail(videoId)}
            async {getRelatedVideo(videoId)}
        }
    }

    private suspend fun getVideoDetail(videoId: String) {
        val retrofit = RetrofitData.initRetrofit()
        val response = retrofit.create(RetrofitService::class.java)
            .getVideoDetail(BuildConfig.API_KEY, "snippet, statistics",videoId)
        if (response.isSuccessful){
            if (response.body()?.items?.size != 0){
                getChannelData(response.body()!!)
            }
        }
    }

    private suspend fun getChannelData(videoDetailResponseData: VideoDetailData) {
        val retrofit = RetrofitData.initRetrofit()
        val response = retrofit.create(RetrofitService::class.java)
            .getChannelData(BuildConfig.API_KEY, "snippet, contentDetails, statistics, brandingSettings"
                ,videoDetailResponseData.items[0].snippet?.channelId)
        if (response.isSuccessful){
            if (response.body()?.items?.size != 0){
                withContext(Dispatchers.Main){
                    detailMapping(videoDetailResponseData, response.body()!!)
                }
            }
        }
    }

    private fun detailMapping(videoDetailResponseData: VideoDetailData, channelDetailResponseData: ChannelSearchData){
        currentChannelSearchData = channelDetailResponseData
        currentVideoDetailData = videoDetailResponseData
        val youtubeDigitConverter = YoutubeDigitConverter(activity)
        relatedVideoRecyclerViewAdapter.setHeaderViewData(HeaderViewData(videoDetailResponseData.items[0].snippet?.title!!,
            youtubeDigitConverter.viewCountCalculator(videoDetailResponseData.items[0].statistics?.viewCount!!),
            youtubeDigitConverter.intervalBetweenDateText(videoDetailResponseData.items[0].snippet?.publishedAt!!),
            channelDetailResponseData.items[0].snippet?.title!!,
            channelDetailResponseData.items[0].snippet?.thumbnails?.high?.url!!,
            youtubeDigitConverter.subscriberCountConverter(channelDetailResponseData.items[0].statistics?.subscriberCount!!)
        ),activity)
    }

    private suspend fun getRelatedVideo(videoId: String) {
        val retrofit = RetrofitYT.initRetrofit()
        val response = retrofit.create(RetrofitService::class.java)
            .getRelatedVideo("id, snippet",videoId,"video","50")
        if (response.isSuccessful) {
            if (response.body()?.RelatedVideoDataitems?.size != 0) {
                withContext(Dispatchers.Main){
                    relatedVideoMapping(response.body()!!)
                }
            }
        }
        else{
            Log.d("안됐음","ㄴㅇㄹㄴㅁㄹ")
        }
    }
    private fun relatedVideoMapping(relatedVideoData: RelatedVideoData){
        relatedVideoList.clear()

        val youtubeDigitConverter = YoutubeDigitConverter(activity)
        for (index in relatedVideoData.RelatedVideoDataitems.indices){
            val thumbnail = relatedVideoData.RelatedVideoDataitems[index].snippet?.thumbnails?.high?.url!!
            val rawDate = relatedVideoData.RelatedVideoDataitems[index].snippet?.publishedAt!!
            val date = youtubeDigitConverter.intervalBetweenDateText(rawDate)
            val title = stringToHtmlSign(relatedVideoData.RelatedVideoDataitems[index].snippet?.title!!)
            val videoId = relatedVideoData.RelatedVideoDataitems[index].id?.videoId!!
            val channelTitle = relatedVideoData.RelatedVideoDataitems[index].snippet?.channelTitle!!
            val channelId = relatedVideoData.RelatedVideoDataitems[index].snippet?.channelId!!
            relatedVideoList.add(VideoData(thumbnail, title, channelTitle, channelId, videoId, date, false))
        }
        binding.relatedVideoProgressBar.visibility = View.GONE
        relatedVideoRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView(){
        binding.fragmentRecyclerView.layoutManager = LinearLayoutManager(activity)
        relatedVideoRecyclerViewAdapter = RelatedVideoRecyclerViewAdapter()
        relatedVideoRecyclerViewAdapter.setItemClickListener(object: RelatedVideoRecyclerViewAdapter.OnItemClickListener{
            override fun channelClick(v: View, position: Int) {
                binding.playerMotionLayout.transitionToState(R.id.start)
                if (currentFragment is HomeFragment){
                    Log.d("눌렀다","homeFragment일 때")
                    currentFragment.childFragmentManager.beginTransaction()
                    .add(currentFragment.binding.searchResultFrameLayout.id,
                        ChannelFragment(channelDataMapper(), currentFragment))
                        .addToBackStack(null)
                        .commit()
                }
                if (currentFragment is MyPlaylistFragment){
                    Log.d("눌렀어","myplaylsitFragment일 때")
                    currentFragment.childFragmentManager.beginTransaction()
                        .add(currentFragment.binding.resultFrameLayout.id,
                        ChannelFragment(channelDataMapper(),currentFragment))
                        .addToBackStack(null)
                        .commit()
                }
            }
            override fun videoClick(v: View, position: Int) {
//                replaceVideo(position)
//                playCurrentVideo()
                replaceVideo(position)
            }
            override fun optionButtonClick(v: View, position: Int) {
            }
        })
        relatedVideoRecyclerViewAdapter.submitList(relatedVideoList)
        binding.fragmentRecyclerView.adapter = relatedVideoRecyclerViewAdapter
    }

    fun channelDataMapper(): ChannelData{
        val channelThumbnail = currentChannelSearchData.items[0].snippet?.thumbnails?.default?.url!!
        val videoCount = currentChannelSearchData.items[0].statistics?.videoCount!!
        val subscriberCount = currentChannelSearchData.items[0].statistics?.subscriberCount!!
        val viewCount = currentChannelSearchData.items[0].statistics?.viewCount!!
        val channelBanner = currentChannelSearchData.items[0].brandingSettings?.image?.bannerExternalUrl
        val channelTitle = currentChannelSearchData.items[0].snippet?.title!!
        val channelDescription = currentChannelSearchData.items[0].snippet?.description!!
        val channelPlaylistId = currentChannelSearchData.items[0].contentDetails?.relatedPlaylists?.uploads!!
        return ChannelData(channelTitle, channelDescription, channelBanner, channelThumbnail, videoCount, viewCount, subscriberCount, channelPlaylistId)
    }

    fun replaceVideo(position: Int){
        binding.fragmentRecyclerView.scrollToPosition(0)
        relatedVideoRecyclerViewAdapter.setHeaderViewTitle(relatedVideoList[position].title)
        relatedVideoRecyclerViewAdapter.setHeaderViewColorBeforeGettingData(activity)
        activity.videoService!!.playVideo(relatedVideoList[position])

        Glide.with(binding.playerThumbnailView)
            .load(relatedVideoList[position].thumbnail)
            .placeholder(R.color.before_getting_data_color)
            .into(binding.playerThumbnailView)
        binding.relatedVideoProgressBar.visibility = View.VISIBLE
        getDetailData(relatedVideoList[position].videoId)

    }


    private fun initView(){
        val currentPlayingVideoData = videoData
        binding.bottomPlayerCloseButton.setOnClickListener {
            activity.supportFragmentManager.beginTransaction().remove(this).commit()
        }
        binding.bottomPlayerPauseButton.setOnClickListener {
            if (player?.isPlaying!!){
                player?.pause()
                binding.bottomPlayerPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            else{
                player?.play()
                binding.bottomPlayerPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }
        binding.bottomTitleTextView.text = currentPlayingVideoData.title

        Glide.with(binding.playerThumbnailView)
            .load(currentPlayingVideoData.thumbnail)
            .placeholder(R.color.before_getting_data_color)
            .into(binding.playerThumbnailView)
    }

    fun settingBottomPlayButton(){
        if (player?.isPlaying!!)
            binding.bottomPlayerPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
        else
            binding.bottomPlayerPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun playerViewInvisibleEvent(){
        binding.playerView.visibility = View.INVISIBLE
        binding.bufferingProgressBar.visibility = View.VISIBLE
        binding.playerThumbnailView.visibility = View.VISIBLE
    }

    fun playerViewVisibleEvent(){
        binding.playerView.visibility = View.VISIBLE
        binding.bufferingProgressBar.visibility = View.GONE
        binding.playerThumbnailView.visibility = View.GONE
    }

//    fun playPrevVideo(){
//        activity.videoService!!.playVideo(playerModel.prevMusic()!!)
//        playerModel.refreshPlaylist()
////            playlistItemsRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//        relatedVideoRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//        updatePlayerView()
//    }

//    fun playNextVideo(){
//        activity.videoService!!.playVideo(playerModel.nextMusic()!!)
//        playerModel.refreshPlaylist()
//
////            playlistItemsRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//
//        relatedVideoRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//        updatePlayerView()
//    }

//    fun playCurrentVideo(){
//        activity.videoService!!.playVideo(playerModel.currentMusicModel()!!)
//    }


//    fun replaceVideo(position: Int){
//        binding.fragmentRecyclerView.scrollToPosition(0)
//        playerModel.updateCurrentPosition(position)
//        playerModel.refreshPlaylist()
////        playlistItemsRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//        relatedVideoRecyclerViewAdapter.submitList(playerModel.getPlayMusicList())
//        updatePlayerView()
//    }


    private fun initListener(){
        playerView = binding.playerView
        player = activity.exoPlayer
        playerView.player = player
        activity.videoService!!.playVideo(videoData)
//        binding.fragmentRecyclerView.scrollToPosition(playerModel.getCurrentPosition())
    }

    private fun initMotionLayout() {
        binding.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {


                (activity).also { main ->
                    main.findViewById<MotionLayout>(mainBinding.mainMotionLayout.id).progress =
                        abs(progress)
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                // 화면이 축소된 상태에서는 엑소 플레이어의 컨트롤러 없애기
                if (binding.playerMotionLayout.currentState == R.id.start){
                    settingBottomPlayButton()
                    binding.playerView.useController = false
                }
                else
                    binding.playerView.useController = true
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("프레그먼트의","onDestroy")
        activity.videoService!!.stopForegroundService()
        fbinding = null
    }
    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
//        binding.fragmentRecyclerView.scrollToPosition(playerModel.getCurrentPosition())
        Log.d("리줌","왜안되지")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.d("프레그먼트","온스탑")
    }

    override fun onStart() {
        super.onStart()
        Log.d("프레그먼트","온스타트")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
    }

    override fun onDetach() {
        super.onDetach()
    }
    private fun stringToHtmlSign(str: String): String {
        return str.replace("&amp;".toRegex(), "[&]")
            .replace("[<]".toRegex(), "&lt;")
            .replace("[>]".toRegex(), "&gt;")
            .replace("&quot;".toRegex(), "'")
            .replace("&#39;".toRegex(), "'")
    }

}