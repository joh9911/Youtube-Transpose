package com.example.video_transpose

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("search")
    suspend fun getVideoDetails(
                        @Query("part") part: String,
                        @Query("q") q: String,
                        @Query("maxResults") maxResults: String,
                        @Query("type") type: String,
                        @Query("pageToken") pageToken: String?
    ): Response<VideoSearchData>

    @GET("search")
    suspend fun getAllSearchData(@Query("key") key: String,
                         @Query("part") part: String,
                         @Query("q") q: String,
                         @Query("maxResults") maxResults: String

    ): Response<AllSearchData>

    @GET("search")
    fun getSuggestionKeyword(@Query("client") client: String,
                             @Query("ds") ds: String,
                             @Query("q") q: String
    ): Call<ResponseBody>

    @GET("playlistItems")
    suspend fun getPlayListVideoItems(@Query("key") key: String,
                         @Query("part") part: String,
                         @Query("playlistId") playListId: String,
                         @Query("pageToken") pageToken: String?,
                         @Query("maxResults") maxResults: String

    ): Response<PlayListVideoSearchData>

    @GET("playlists")
    suspend fun getPlayLists(@Query("key") key: String,
                     @Query("part") part: String,
                     @Query("id") id: String, // id는 각 플레이리스트의 아이디
                     @Query("maxResults") maxResults: String
    ): Response<PlayListSearchData>
//    @Query("key") key: String,
    @GET("channels")
    suspend fun getChannelData(
                       @Query("part") part: String,
                       @Query("id") id: String?
    ): Response<ChannelSearchData>
}

/**
 * video만 찾아서 저장하는 클래스
 */
data class VideoSearchData (
    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("regionCode"    ) var regionCode    : String?          = null,
    @SerializedName("items"         ) var items         : ArrayList<VideoItems> = arrayListOf()
)
data class VideoItems (
    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : VideoId?      = VideoId(),
    @SerializedName("snippet" ) var snippet : VideoSnippet? = VideoSnippet()
)
data class VideoSnippet (
    @SerializedName("publishedAt"          ) var publishedAt          : String?     = null,
    @SerializedName("channelId"            ) var channelId            : String?     = null,
    @SerializedName("title"                ) var title                : String?     = null,
    @SerializedName("description"          ) var description          : String?     = null,
    @SerializedName("thumbnails"           ) var thumbnails           : VideoThumbnails? = VideoThumbnails(),
    @SerializedName("channelTitle"         ) var channelTitle         : String?     = null,
    @SerializedName("liveBroadcastContent" ) var liveBroadcastContent : String?     = null,
    @SerializedName("publishTime"          ) var publishTime          : String?     = null
)
data class VideoThumbnails (
    @SerializedName("default" ) var default : VideoDefault? = VideoDefault(),
    @SerializedName("medium"  ) var medium  : VideoMedium?  = VideoMedium(),
    @SerializedName("high"    ) var high    : VideoHigh?    = VideoHigh()
)
data class VideoHigh (
    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null
)
data class VideoMedium (
    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null
)
data class VideoDefault (
    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null
)
data class VideoId (
    @SerializedName("kind"    ) var kind    : String? = null,
    @SerializedName("videoId" ) var videoId : String? = null
)

/**
 * 해당 플레이리스트의 비디오들을 저장하는 데이터 클래스
 */
data class PlayListVideoSearchData (

    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("items"         ) var items         : ArrayList<PlayListVideoItems> = arrayListOf(),
    @SerializedName("pageInfo"      ) var pageInfo      : PlayListVideoPageInfo?        = PlayListVideoPageInfo()

)
data class PlayListVideoDefault (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListVideoMedium (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListVideoHigh (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListVideoStandard (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListVideoMaxres (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListVideoThumbnails (

    @SerializedName("default"  ) var default  : PlayListVideoDefault?  = PlayListVideoDefault(),
    @SerializedName("medium"   ) var medium   : PlayListVideoMedium?   = PlayListVideoMedium(),
    @SerializedName("high"     ) var high     : PlayListVideoHigh?     = PlayListVideoHigh(),
    @SerializedName("standard" ) var standard : PlayListVideoStandard? = PlayListVideoStandard(),
    @SerializedName("maxres"   ) var maxres   : PlayListVideoMaxres?   = PlayListVideoMaxres()

)
data class PlayListVideoResourceId (

    @SerializedName("kind"    ) var kind    : String? = null,
    @SerializedName("videoId" ) var videoId : String? = null

)
data class PlayListVideoSnippet (

    @SerializedName("publishedAt"            ) var publishedAt            : String?     = null,
    @SerializedName("channelId"              ) var channelId              : String?     = null,
    @SerializedName("title"                  ) var title                  : String?     = null,
    @SerializedName("description"            ) var description            : String?     = null,
    @SerializedName("thumbnails"             ) var thumbnails             : PlayListVideoThumbnails? = PlayListVideoThumbnails(),
    @SerializedName("channelTitle"           ) var channelTitle           : String?     = null,
    @SerializedName("playlistId"             ) var playlistId             : String?     = null,
    @SerializedName("position"               ) var position               : Int?        = null,
    @SerializedName("resourceId"             ) var resourceId             : PlayListVideoResourceId? = PlayListVideoResourceId(),
    @SerializedName("videoOwnerChannelTitle" ) var videoOwnerChannelTitle : String?     = null,
    @SerializedName("videoOwnerChannelId"    ) var videoOwnerChannelId    : String?     = null

)
data class PlayListVideoItems (

    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : String?  = null,
    @SerializedName("snippet" ) var snippet : PlayListVideoSnippet? = PlayListVideoSnippet()

)
data class PlayListVideoPageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)

/**
 * 플레이리스트 정보를 저장하는 데이터 클래스
 */
data class PlayListSearchData (

    @SerializedName("kind"     ) var kind     : String?          = null,
    @SerializedName("etag"     ) var etag     : String?          = null,
    @SerializedName("pageInfo" ) var pageInfo : PlayListPageInfo?        = PlayListPageInfo(),
    @SerializedName("items"    ) var items    : ArrayList<PlayListItems> = arrayListOf()

)
data class PlayListPageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)
data class PlayListMedium (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListStandard (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListMaxres (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class PlayListThumbnails (

    @SerializedName("medium"   ) var medium   : PlayListMedium?   = PlayListMedium(),
    @SerializedName("standard" ) var standard : PlayListStandard? = PlayListStandard(),
    @SerializedName("maxres"   ) var maxres   : PlayListMaxres?   = PlayListMaxres()

)
data class PlayListLocalized (

    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null

)
data class PlayListSnippet (

    @SerializedName("publishedAt"     ) var publishedAt     : String?     = null,
    @SerializedName("channelId"       ) var channelId       : String?     = null,
    @SerializedName("title"           ) var title           : String?     = null,
    @SerializedName("description"     ) var description     : String?     = null,
    @SerializedName("thumbnails"      ) var thumbnails      : PlayListThumbnails? = PlayListThumbnails(),
    @SerializedName("channelTitle"    ) var channelTitle    : String?     = null,
    @SerializedName("defaultLanguage" ) var defaultLanguage : String?     = null,
    @SerializedName("localized"       ) var localized       : PlayListLocalized?  = PlayListLocalized()

)
data class PlayListItems (

    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : String?  = null,
    @SerializedName("snippet" ) var snippet : PlayListSnippet? = PlayListSnippet()

)

/**
 * 그냥 유튜브 검색시 모든 검색 결과를 저장하는 데이터 클래스
 */
data class AllSearchData (

    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("regionCode"    ) var regionCode    : String?          = null,
    @SerializedName("pageInfo"      ) var pageInfo      : PageInfo?        = PageInfo(),
    @SerializedName("items"         ) var items         : ArrayList<Items> = arrayListOf()

)

data class PageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)

data class Id (

    @SerializedName("kind"      ) var kind      : String? = null,
    @SerializedName("channelId" ) var channelId : String? = null

)

data class Default (

    @SerializedName("url" ) var url : String? = null

)

data class Medium (

    @SerializedName("url" ) var url : String? = null

)

data class High (

    @SerializedName("url" ) var url : String? = null

)

data class Thumbnails (

    @SerializedName("default" ) var default : Default? = Default(),
    @SerializedName("medium"  ) var medium  : Medium?  = Medium(),
    @SerializedName("high"    ) var high    : High?    = High()

)

data class Snippet (

    @SerializedName("publishedAt"          ) var publishedAt          : String?     = null,
    @SerializedName("channelId"            ) var channelId            : String?     = null,
    @SerializedName("title"                ) var title                : String?     = null,
    @SerializedName("description"          ) var description          : String?     = null,
    @SerializedName("thumbnails"           ) var thumbnails           : Thumbnails? = Thumbnails(),
    @SerializedName("channelTitle"         ) var channelTitle         : String?     = null,
    @SerializedName("liveBroadcastContent" ) var liveBroadcastContent : String?     = null,
    @SerializedName("publishTime"          ) var publishTime          : String?     = null

)

data class Items (

    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : Id?      = Id(),
    @SerializedName("snippet" ) var snippet : Snippet? = Snippet()

)

/**
 * 채널 정보
 */
data class ChannelSearchData (

    @SerializedName("kind"     ) var kind     : String?          = null,
    @SerializedName("etag"     ) var etag     : String?          = null,
    @SerializedName("pageInfo" ) var pageInfo : ChannelPageInfo? = ChannelPageInfo(),
    @SerializedName("items"    ) var items    : ArrayList<ChannelItems> = arrayListOf()

)

data class ChannelPageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)

data class ChannelDefault (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class ChannelMedium (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)
data class ChannelHigh (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class ChannelThumbnails (

    @SerializedName("default" ) var default : ChannelDefault? = ChannelDefault(),
    @SerializedName("medium"  ) var medium  : ChannelMedium?  = ChannelMedium(),
    @SerializedName("high"    ) var high    : ChannelHigh?    = ChannelHigh()

)

data class ChannelLocalized (

    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null

)

data class ChannelSnippet (

    @SerializedName("title"           ) var title           : String?     = null,
    @SerializedName("description"     ) var description     : String?     = null,
    @SerializedName("customUrl"       ) var customUrl       : String?     = null,
    @SerializedName("publishedAt"     ) var publishedAt     : String?     = null,
    @SerializedName("thumbnails"      ) var thumbnails      : ChannelThumbnails? = ChannelThumbnails(),
    @SerializedName("defaultLanguage" ) var defaultLanguage : String?     = null,
    @SerializedName("localized"       ) var localized       : ChannelLocalized?  = ChannelLocalized()

)

data class ChannelRelatedPlaylists (

    @SerializedName("likes"   ) var likes   : String? = null,
    @SerializedName("uploads" ) var uploads : String? = null

)

data class ChannelContentDetails (

    @SerializedName("relatedPlaylists" ) var relatedPlaylists : ChannelRelatedPlaylists? = ChannelRelatedPlaylists()

)

data class ChannelStatistics (

    @SerializedName("viewCount"             ) var viewCount             : String?  = null,
    @SerializedName("subscriberCount"       ) var subscriberCount       : String?  = null,
    @SerializedName("hiddenSubscriberCount" ) var hiddenSubscriberCount : Boolean? = null,
    @SerializedName("videoCount"            ) var videoCount            : String?  = null

)

data class ChannelChannel (

    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("keywords"    ) var keywords    : String? = null

)

data class ChannelImage (

    @SerializedName("bannerExternalUrl" ) var bannerExternalUrl : String? = null

)

data class ChannelBrandingSettings (

    @SerializedName("channel" ) var channel : ChannelChannel? = ChannelChannel(),
    @SerializedName("image"   ) var image   : ChannelImage?   = ChannelImage()

)

data class ChannelItems (

    @SerializedName("kind"           ) var kind           : String?         = null,
    @SerializedName("etag"           ) var etag           : String?         = null,
    @SerializedName("id"             ) var id             : String?         = null,
    @SerializedName("snippet"        ) var snippet        : ChannelSnippet?        = ChannelSnippet(),
    @SerializedName("contentDetails" ) var contentDetails : ChannelContentDetails? = ChannelContentDetails(),
    @SerializedName("statistics"     ) var statistics     : ChannelStatistics?     = ChannelStatistics(),
    @SerializedName("brandingSettings" ) var brandingSettings : ChannelBrandingSettings? = ChannelBrandingSettings()
)

data class YTVideoData (

    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("items"         ) var items         : ArrayList<YTVideoItems> = arrayListOf()

)

data class YTVideoId (

    @SerializedName("kind"    ) var kind    : String? = null,
    @SerializedName("videoId" ) var videoId : String? = null

)

data class YTVideoThumbnails (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)


data class YTVideoChannelThumbnails (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class YTVideoDetailedMetadataSnippet (

    @SerializedName("text" ) var text : String? = null

)

data class YTVideoSnippet (

    @SerializedName("channelId"               ) var channelId               : String?                            = null,
    @SerializedName("title"                   ) var title                   : String?                            = null,
    @SerializedName("thumbnails"              ) var thumbnails              : ArrayList<YTVideoThumbnails>              = arrayListOf(),
    @SerializedName("channelTitle"            ) var channelTitle            : String?                            = null,
    @SerializedName("channelHandle"           ) var channelHandle           : String?                            = null,
    @SerializedName("timestamp"               ) var timestamp               : String?                            = null,
    @SerializedName("duration"                ) var duration                : Int?                               = null,
    @SerializedName("views"                   ) var views                   : Int?                               = null,
    @SerializedName("badges"                  ) var badges                  : ArrayList<String>                  = arrayListOf(),
    @SerializedName("channelApproval"         ) var channelApproval         : String?                            = null,
    @SerializedName("channelThumbnails"       ) var channelThumbnails       : ArrayList<YTVideoChannelThumbnails>       = arrayListOf(),
    @SerializedName("detailedMetadataSnippet" ) var detailedMetadataSnippet : ArrayList<YTVideoDetailedMetadataSnippet> = arrayListOf()

)

data class YTVideoItems (

    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : YTVideoId?      = YTVideoId(),
    @SerializedName("snippet" ) var snippet : YTVideoSnippet? = YTVideoSnippet()

)
data class VideoData(
    val thumbnail: String,
    val title: String,
    val channel: String,
    val videoId: String,
    val date: String,
    val channelThumbnail: String?,
    val isPlaying: Boolean
)

data class PlayListData(
    val thumbnail: String,
    val title: String,
    val description: String,
    val playlistId: String
)

data class ChannelData(
    val channelTitle: String,
    val channelDescription: String,
    val channelBanner: String?,
    val channelThumbnail: String?,
    val channelVideoCount: String,
    val channelViewCount: String,
    val channelSubscriberCount: String,
    val channelPlaylistId: String
)