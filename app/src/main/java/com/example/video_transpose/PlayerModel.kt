package com.example.video_transpose

import android.util.Log

data class PlayerModel (
    private var playMusicList: List<VideoData> = emptyList(),
    private var currentPosition: Int = -1, // -1: 초기화 되지 않은 값
){
    init {
        Log.d("ㅑㅜㅑㅅ","init함수")
        refreshPlaylist()
    }
    fun getCurrentPosition(): Int{
        return currentPosition
    }

    fun getPlayMusicList(): List<VideoData>{
        return playMusicList
    }

    // 가져 갈 때마다 position 위치 보고 반환
    fun refreshPlaylist() {
        playMusicList = playMusicList.mapIndexed { index, musicModel ->
            if (musicModel == VideoData(" ", " ", " ", " ", " ", " ", false)){
                Log.d("모델 조건","문")
                return
            }

            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            Log.d("새로운 아이템","${newItem}")
            newItem
        }
    }

    fun updateCurrentPosition(position: Int) {
        currentPosition = position
    }

    fun nextMusic(): VideoData? {
        if (playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) 0 else currentPosition + 1

        return playMusicList[currentPosition]
    }

    fun prevMusic(): VideoData? {
        if (playMusicList.isEmpty()) return null

        // kotlin 의 lastIndex 사용 해보기
        currentPosition = if ((currentPosition - 1) < 0) playMusicList.lastIndex else currentPosition - 1

        return playMusicList[currentPosition]
    }

    fun currentMusicModel(): VideoData? {
        if (playMusicList.isEmpty())
            return null
        return playMusicList[currentPosition]
    }
}