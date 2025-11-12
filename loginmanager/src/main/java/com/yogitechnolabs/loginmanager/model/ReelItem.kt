package com.yogitechnolabs.loginmanager.model

data class ReelItem(
    val videoUrl                    :                    String,
    val title                       :                    String ,
    val description                 :                    String,
    var isLiked                     :                    Boolean = false
)

