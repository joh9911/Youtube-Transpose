package com.myFile.Transpose

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.myFile.Transpose.databinding.MainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*


class Activity: AppCompatActivity() {
    var mBinding: MainBinding? = null
    val binding get() = mBinding!!

    lateinit var exoPlayer: ExoPlayer
    lateinit var transposePage: LinearLayout

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var pitchSeekBar: SeekBar
    lateinit var tempoSeekBar: SeekBar
    lateinit var homeFragment: HomeFragment
    lateinit var myPlaylistFragment: MyPlaylistFragment
    var videoService: VideoService? = null

    private lateinit var connection: NetworkConnection

    private val bindConnection = object: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            val b = p1 as VideoService.VideoServiceBinder
            videoService = b.getService()
            videoService!!.initActivity(this@Activity)
            exoPlayer = videoService!!.VideoServiceBinder().getExoPlayerInstance()

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler
    val API_KEY = com.myFile.Transpose.BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initExceptionHandler()
        connection = NetworkConnection(this)
        bindService(Intent(this, VideoService::class.java), bindConnection, BIND_AUTO_CREATE)
        connection.observe(this, Observer { isConnected ->
            if (isConnected)
            {
            } else
            {
                Log.d("네트워크 연결 안됨","ㅁㄴㅇㄹ")
            }
        })
        initFragment()
    }
    fun initFragment(){
        homeFragment = HomeFragment()
        myPlaylistFragment = MyPlaylistFragment()
        supportFragmentManager.beginTransaction()
            .add(binding.basicFrameLayout.id,homeFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(binding.basicFrameLayout.id,myPlaylistFragment)
            .commit()
        supportFragmentManager.beginTransaction().hide(myPlaylistFragment).commit()
        supportFragmentManager.beginTransaction().show(homeFragment).commit()
    }

    private fun initExceptionHandler(){
        coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
            Log.d("코루틴 에러","$throwable")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(this@Activity,resources.getString(R.string.network_error_message),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView() {
        initTranspose()
        initBottomNavigationView()
    }
    fun initTranspose(){
        transposePage = binding.transposePage
        pitchSeekBar = binding.pitchSeekBar
        pitchSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.pitchValue.text = p1.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                for(fragment in supportFragmentManager.fragments) {
                    if(fragment.isVisible && fragment is PlayerFragment) {
                        videoService!!.setPitch(p0?.progress!!)
                    }
                }
            }
        })
        binding.pitchInitButton.setOnClickListener {
            pitchSeekBar.progress = 0
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setPitch(pitchSeekBar.progress)
                }
            }
        }
        binding.pitchSeekBarMinusButton.setOnClickListener {
            pitchSeekBar.progress -= 1
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setPitch(pitchSeekBar.progress)
                }
            }
        }
        binding.pitchSeekBarPlusButton.setOnClickListener {
            pitchSeekBar.progress += 1
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setPitch(pitchSeekBar.progress)
                }
            }
        }
        tempoSeekBar = binding.tempoSeekBar
        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tempoValue.text = p1.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                for(fragment in supportFragmentManager.fragments) {
                    if(fragment.isVisible && fragment is PlayerFragment) {
                        videoService!!.setTempo(p0?.progress!!)
                    }
                }

            }
        })
        binding.tempoInitButton.setOnClickListener {
            tempoSeekBar.progress = 0
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setTempo(tempoSeekBar.progress)
                }
            }
        }
        binding.tempoSeekBarMinusButton.setOnClickListener {
            tempoSeekBar.progress -= 1
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setTempo(tempoSeekBar.progress)
                }
            }
        }
        binding.tempoSeekBarPlusButton.setOnClickListener {
            tempoSeekBar.progress += 1
            for(fragment in supportFragmentManager.fragments) {
                if(fragment.isVisible && fragment is PlayerFragment) {
                    videoService!!.setTempo(tempoSeekBar.progress)
                }
            }
        }
        binding.transposeBackButton.setOnClickListener {
            transposePageInvisibleEvent()
        }

        binding.transposePage.setOnClickListener {
            Log.d("asdf","ASdf")
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("activity","onPause")
    }

    fun initBottomNavigationView(){
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_icon -> {
                    if (homeFragment.isVisible){
                        if (transposePage.visibility == View.VISIBLE)
                            transposePage.visibility = View.INVISIBLE
                        else
                            homeFragment.childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    else{
                        supportFragmentManager.beginTransaction().hide(myPlaylistFragment).commit()
                        supportFragmentManager.beginTransaction().show(homeFragment).commit()
                        transposePage.visibility = View.INVISIBLE

                    }
                }
                R.id.transpose_icon -> {
                    transposePage.visibility = View.VISIBLE
                }
                R.id.my_playlist_icon -> {
                    if (myPlaylistFragment.isVisible){
                        if (transposePage.visibility == View.VISIBLE)
                            transposePage.visibility = View.INVISIBLE
                        else
                            myPlaylistFragment.childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    else{
                        supportFragmentManager.beginTransaction().show(myPlaylistFragment).commit()
                        supportFragmentManager.beginTransaction().hide(homeFragment).commit()
                        transposePage.visibility = View.INVISIBLE

                    }
                }
            }
            true
        }
    }

    fun transposePageInvisibleEvent(){
            transposePage.visibility = View.INVISIBLE
            binding.bottomNavigationView.menu.findItem(R.id.home_icon).isChecked = true
    }

    override fun onBackPressed() {
        Log.d("액티비티의","백프레스")
        for (fragment: Fragment in supportFragmentManager.fragments){
            if (fragment is PlayerFragment && fragment.binding.playerMotionLayout.currentState == R.id.end)
                return super.onBackPressed()
        }
        if (transposePage.visibility == View.VISIBLE)
            transposePageInvisibleEvent()
        else
            return super.onBackPressed()
//        if (supportFragmentManager.findFragmentById(R.id.player_fragment) == null){
//            if (transposePage.visibility == View.VISIBLE)
//                transposePageInvisibleEvent()
//            else
//                return super.onBackPressed()
//        }
//        else{
//            val playerFragment = supportFragmentManager.findFragmentById(binding.playerFragment.id) as PlayerFragment
//            if (playerFragment.binding.playerMotionLayout.currentState == R.id.end){
//                playerFragment.binding.playerMotionLayout.transitionToState(R.id.start)
//            }
//            else{
//                if (transposePage.visibility == View.VISIBLE)
//                    transposePageInvisibleEvent()
//                else
//                    return super.onBackPressed()
//            }
//        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("액티비티의","onDestroy")
        unbindService(bindConnection)
        val intent = Intent(this, VideoService::class.java)
        stopService(intent)
    }

}