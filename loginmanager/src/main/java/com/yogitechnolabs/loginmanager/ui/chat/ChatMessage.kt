package com.yogitechnolabs.loginmanager.ui.chat

data class ChatMessage(
    val id: String,
    val text: String,
    val isSentByUser: Boolean,
    val timestamp: Long,
    var status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENT,
    DELIVERED,
    SEEN
}

