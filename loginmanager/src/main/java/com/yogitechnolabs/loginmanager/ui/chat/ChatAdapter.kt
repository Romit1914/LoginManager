package com.yogitechnolabs.loginmanager.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_SENT = 1
    private val TYPE_RECEIVED = 2
    private val TYPE_TYPING = 3

    val messages = mutableListOf<ChatMessage>()
    var isTypingVisible = false

    override fun getItemViewType(position: Int): Int {
        return when {
            isTypingVisible && position == messages.size -> TYPE_TYPING
            messages[position].isSentByUser -> TYPE_SENT
            else -> TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SENT -> SentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false))
            TYPE_RECEIVED -> ReceivedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false))
            TYPE_TYPING -> TypingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_typing_indicator, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = messages.size + if (isTypingVisible) 1 else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SentViewHolder) {
            val msg = messages[position]
            holder.textMessage.text = msg.text

            when (msg.status) {
                MessageStatus.SENT -> {
                    holder.textStatus.text = "Sent"
                    holder.textStatus.setTextColor(Color.GRAY)
                    holder.imageStatus.setImageResource(R.drawable.ic_sent)
                    holder.imageStatus.setColorFilter(Color.GRAY)
                }
                MessageStatus.DELIVERED -> {
                    holder.textStatus.text = "Delivered"
                    holder.textStatus.setTextColor(Color.DKGRAY)
                    holder.imageStatus.setImageResource(R.drawable.ic_delivered)
                    holder.imageStatus.setColorFilter(Color.DKGRAY)
                }
                MessageStatus.SEEN -> {
                    holder.textStatus.text = "Seen"
                    holder.textStatus.setTextColor(Color.BLUE)
                    holder.imageStatus.setImageResource(R.drawable.ic_seen)
                    holder.imageStatus.setColorFilter(Color.BLUE)
                }
            }
        } else if (holder is ReceivedViewHolder) {
            val msg = messages[position]
            holder.textMessage.text = msg.text
        } else if (holder is TypingViewHolder) {
            // no dynamic content needed
        }
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setTypingVisible(visible: Boolean) {
        if (isTypingVisible == visible) return
        isTypingVisible = visible
        if (visible) notifyItemInserted(messages.size)
        else notifyItemRemoved(messages.size)
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val imageStatus: ImageView = itemView.findViewById(R.id.imageStatus)
    }

    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
    }

    inner class TypingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTyping: TextView = itemView.findViewById(R.id.textTyping)
    }
}
