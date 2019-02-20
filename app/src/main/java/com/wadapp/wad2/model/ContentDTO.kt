package com.wadapp.lsm.wad.model

import java.util.HashMap

data class ContentDTO(var explain: String? = null,
                      var imageUrl: String? = null,
                      var username: String? = null,
                      var uid: String? = null,
                      var userId: String? = null,
                      var todayword: String? = null,
                      var timestamp: Long? = null,
                      var favoriteCount: Int = 0,
                      var commentCount : Int = 0,
                      var favorites: MutableMap<String, Boolean> = HashMap()
) {

    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var documentid: String? =null,
                       var username: String? = null,
                       var timestamp: Long? = null)
}