package com.myFile.Transpose

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.myFile.Transpose.databinding.FragmentMyPlaylistSortBinding
import com.myFile.Transpose.databinding.MainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPlaylistFragment: Fragment() {
    lateinit var mainBinding: MainBinding
    var fbinding: FragmentMyPlaylistSortBinding? = null
    val binding get() = fbinding!!
    lateinit var activity: Activity

    lateinit var playlistToolBar: androidx.appcompat.widget.Toolbar
    val suggestionKeywords = ArrayList<String>()
    var myPlaylists = listOf<MyPlaylist>()
    var myMusics = listOf<Musics>()

    private lateinit var searchAdapter: SearchSuggestionKeywordRecyclerViewAdapter
    private lateinit var myPlaylistRecyclerViewAdapter: MyPlaylistRecyclerViewAdapter
    lateinit var searchView: SearchView
    lateinit var searchViewItem: MenuItem
    private lateinit var callback: OnBackPressedCallback
    lateinit var frameLayout: FrameLayout
    lateinit var db: AppDatabase
    lateinit var myPlaylistDao: MyPlaylistDao


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fbinding = FragmentMyPlaylistSortBinding.inflate(inflater, container, false)
        mainBinding = MainBinding.inflate(layoutInflater)
//        setHasOptionsMenu(true)
        val view = binding.root
        initRecyclerView()
        initToolbar()
        Log.d("실행됨","ㄴㅇㄹㄴㅇㄹ")
        frameLayout = binding.resultFrameLayout
        initDb()
        addPlaylistButtonEvent()
        initPlaylistRecyclerView()
        getMyPlaylist()
        return view
    }
    fun getMyPlaylist(){
        CoroutineScope(Dispatchers.IO).launch{
            Log.d("정볼르 받ㄷ","다옴")
            myPlaylists = myPlaylistDao.getAll()
            withContext(Dispatchers.Main){
                Log.d("제출","햇나")
                myPlaylistRecyclerViewAdapter.submitList(myPlaylists.toMutableList())
            }
        }
    }
    fun deleteAndRefreshMyPlaylist(position: Int){
        Log.d("delete","${myPlaylists[position]}")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("코루틴","1")
            myPlaylistDao.delete(myPlaylists[position])
            myPlaylists = myPlaylistDao.getAll()
            withContext(Dispatchers.Main){
                myPlaylistRecyclerViewAdapter.submitList(myPlaylists.toMutableList())
            }
            Log.d("코루틴","2")
        }
    }
    fun initPlaylistRecyclerView(){
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(activity)
        myPlaylistRecyclerViewAdapter = MyPlaylistRecyclerViewAdapter()
        myPlaylistRecyclerViewAdapter.setItemClickListener(object: MyPlaylistRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                childFragmentManager.beginTransaction()
                    .replace(binding.resultFrameLayout.id,MyPlaylistItemsFragment(myPlaylists[position].uid))
                    .addToBackStack(null)
                    .commit()
            }

            override fun optionButtonClick(v: View, position: Int) {
                Log.d("버튼번호","$position")
                val popUp = PopupMenu(activity, v)
                popUp.menuInflater.inflate(R.menu.my_playlist_pop_up_menu, popUp.menu)
                popUp.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_my_playlist -> {
                            deleteAndRefreshMyPlaylist(position)
                        }
                    }
                    true
                })
                popUp.show()
            }
        })
        binding.playlistRecyclerView.adapter = myPlaylistRecyclerViewAdapter
    }
    fun initRecyclerView(){
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)
        searchAdapter = SearchSuggestionKeywordRecyclerViewAdapter()
        searchAdapter.setItemClickListener(object: SearchSuggestionKeywordRecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val searchWord = suggestionKeywords[position]
                suggestionKeywords.clear()
                searchAdapter.submitList(suggestionKeywords.toMutableList())
                searchView.setQuery(searchWord,false) // 검색한 키워드 텍스트 설정
                searchView.clearFocus()
                childFragmentManager.beginTransaction()
                    .replace(binding.resultFrameLayout.id,SearchResultFragment(
                        searchWord,
                        frameLayout,
                        searchView,
                    ))
                    .addToBackStack(null)
                    .commit()
                binding.searchRecyclerView.visibility = View.INVISIBLE
            }
        })
        binding.searchRecyclerView.adapter = searchAdapter
    }

    fun initDb(){
        db = Room.databaseBuilder(
            activity,
            AppDatabase::class.java, "database-name"
        )
            .build()
        myPlaylistDao = db.myPlaylistDao()

    }
    fun addPlaylistButtonEvent(){

        binding.addPlaylistLinearLayout.setOnClickListener {
            showNoticeDialog()
        }
    }
    fun showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        val dialog = DialogCreatePlaylist()
        dialog.setListener(object: DialogCreatePlaylist.NoticeDialogListener{
            override fun onDialogPositiveClick(dialog: DialogFragment, text: Editable?) {
                CoroutineScope(Dispatchers.IO).launch{
                    myPlaylistDao.insertAll(MyPlaylist(0,"$text"))
                    myPlaylists = myPlaylistDao.getAll()
                    withContext(Dispatchers.Main){
                        myPlaylistRecyclerViewAdapter.submitList(myPlaylists.toMutableList())
                    }
                }
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) {
                dialog.dismiss()}

        })
        dialog.show(activity.supportFragmentManager, "NoticeDialogFragment")
    }

    fun initToolbar(){
        playlistToolBar = binding.playlistToolBar
        val menu = playlistToolBar.menu
        menu.findItem(R.id.transpose_icon).setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {
            Log.d("사이즈","$myPlaylists")
            true
        })
        searchViewItem = menu.findItem(R.id.search_icon)
        searchView = searchViewItem.actionView as SearchView
        searchView.setIconifiedByDefault(true)
        val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        val searchViewCloseButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchViewBackButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchAutoComplete.setTextColor(resources.getColor(R.color.white))
        searchAutoComplete.setHintTextColor(resources.getColor(R.color.white))
        searchAutoComplete.hint = resources.getString(R.string.searchView_hint)
        searchViewCloseButton.setColorFilter(resources.getColor(R.color.white))
        searchViewBackButton.setColorFilter(resources.getColor(R.color.white))
        searchView.setOnQueryTextFocusChangeListener { p0, p1 -> // 서치뷰 검색창을 클릭할 때 이벤트
            if (p1){
                Log.d("눌림","ㄴㅇㄹ")
                binding.searchRecyclerView.visibility = View.VISIBLE
            }
        }
        searchViewItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

                if (activity.transposePage.visibility == View.VISIBLE){
                    activity.transposePage.visibility = View.INVISIBLE
                    activity.binding.bottomNavigationView.menu.findItem(R.id.home_icon).isChecked =
                        true
                }
                Log.d("호갖","장")
                activity.binding.bottomNavigationView.visibility = View.GONE
                return true
            }
            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                Log.d("playlist","서치뷰닫힘")
                var count = 0
                childFragmentManager.addOnBackStackChangedListener {
                    count = childFragmentManager.backStackEntryCount
                }
                searchViewCollapseEvent()
                return count == 0
//                if (activity.supportFragmentManager.findFragmentById(R.id.player_fragment) == null) {
//                    return if (activity.transposePage.visibility == View.VISIBLE) {
//                        Log.d("1","1")
//                        activity.transposePageInvisibleEvent()
//                        false
//                    } else{
//                        searchViewCollapseEvent()
//                        true
//                    }
//                } else {
//                    val playerFragment =
//                        activity.supportFragmentManager.findFragmentById(activity.binding.playerFragment.id) as PlayerFragment
//                    return if (playerFragment.binding.playerMotionLayout.currentState == R.id.end) {
//                        playerFragment.binding.playerMotionLayout.transitionToState(R.id.start)
//                        false
//                    } else {
//                        if (activity.transposePage.visibility == View.VISIBLE) {
//                            Log.d("1","2")
//                            activity.transposePageInvisibleEvent()
//                            false
//                        } else{
//                            searchViewCollapseEvent()
//                            true
//                        }
//                    }
//                }
            }
        })
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("playlist","쿼리를 보냈어요")
                Log.d("playlist","${searchView.query}")
                searchView.clearFocus()
                childFragmentManager.beginTransaction()
                    .replace(binding.resultFrameLayout.id,SearchResultFragment(
                        query!!,
                        frameLayout,
                        searchView
                    ))
                    .addToBackStack(null)
                    .commit()
                binding.searchRecyclerView.visibility = View.INVISIBLE
                return false
            }
            //SwipeRefreshLayout 새로고침
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != ""){
                    getSuggestionKeyword(newText!!)
                }
                if (newText == ""){
                    Log.d("빈","칸")
                    suggestionKeywords.clear()
                    searchAdapter.submitList(suggestionKeywords.toMutableList())
                }
                return false
            }
        })
    }


    private fun getSuggestionKeyword(newText: String){
        val retrofit = RetrofitSuggestionKeyword.initRetrofit()
        retrofit.create(RetrofitService::class.java).getSuggestionKeyword("firefox","yt",newText)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    suggestionKeywords.clear()
                    val responseString = convertStringUnicodeToKorean(response.body()?.string()!!)
                    val splitBracketList = responseString.split('[')
                    val splitCommaList = splitBracketList[2].split(',')
                    if (splitCommaList[0] != "]]" && splitCommaList[0] != '"'.toString()){
                        addSubstringToSuggestionKeyword(splitCommaList)
                    }
                    searchAdapter.submitList(suggestionKeywords.toMutableList())
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("실패!","!1")
                }
            })
    }

    /**
    문자열 정보가 이상하게 들어와 알맞게 나눠주고 리스트에 추가
     **/
    private fun addSubstringToSuggestionKeyword(splitList: List<String>){
        for (index in splitList.indices){
            if (splitList[index].isNotEmpty()){
                if (splitList[index][splitList[index].length-1] == ']')
                    suggestionKeywords.add(splitList[index].substring(1, splitList[index].length-2))
                else
                    suggestionKeywords.add(splitList[index].substring(1, splitList[index].length-1))
            }
        }
    }
    private fun convertStringUnicodeToKorean(data: String): String {
        val sb = StringBuilder() // 단일 쓰레드이므로 StringBuilder 선언
        var i = 0
        /**
         * \uXXXX 로 된 아스키코드 변경
         * i+2 to i+6 을 16진수의 int 계산 후 char 타입으로 변환
         */
        while (i < data.length) {
            if (data[i] == '\\' && data[i + 1] == 'u') {
                val word = data.substring(i + 2, i + 6).toInt(16).toChar()
                sb.append(word)
                i += 5
            } else {
                sb.append(data[i])
            }
            i++
        }

        return sb.toString()
    }

    private fun searchViewCollapseEvent(){
        Log.d("이게 실행이 됏잖아","왜")
//        binding.toolBar.setBackgroundColor(resources.getColor(R.color.black))
        activity.binding.bottomNavigationView.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.INVISIBLE
        suggestionKeywords.clear()
        searchAdapter.submitList(suggestionKeywords.toMutableList())

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
//            Log.d("playlist","hidden ${searchView.query}")
            callback.remove()
        }
        else{
            if (childFragmentManager.backStackEntryCount != 0)
                activity.onBackPressedDispatcher.addCallback(this, callback)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("myPlaylistFragment","backPress")
                if (childFragmentManager.backStackEntryCount == 0) {
                    if (searchViewItem.isActionViewExpanded)
                        searchViewItem.collapseActionView()
                }
                else
                    childFragmentManager.popBackStack()
            }
        }
        childFragmentManager.addOnBackStackChangedListener {
            if (childFragmentManager.backStackEntryCount == 0) {
                if (searchViewItem.isActionViewExpanded)
                    searchViewItem.collapseActionView()

                callback.remove()
            }
            else
                activity.onBackPressedDispatcher.addCallback(this, callback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }
}