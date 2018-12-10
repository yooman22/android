package com.example.yujaeman.howl.model

import java.sql.Timestamp

data class ContentDTO(var explain: String? = null,
                       var imageUrl: String? = null,
                       var uio : String?= null,
                       var userId : String?= null,
                       var timestamp: Long?= null,
                       var favoriteCount : Int = 0,
                       var favorite : Map<String,Boolean> = HashMap()){

    data class Comment(var uid : String?= null,
                        var userId : String?=null,
                        var commnet : String? = null,
                        var timestamp: Long? = null)
}