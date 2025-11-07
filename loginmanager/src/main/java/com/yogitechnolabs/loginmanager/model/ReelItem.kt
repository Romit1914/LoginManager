package com.yogitechnolabs.loginmanager.model

data class ReelItem(
    val videoUrl: String,
    val description: String,
    var isLiked: Boolean = false
)

