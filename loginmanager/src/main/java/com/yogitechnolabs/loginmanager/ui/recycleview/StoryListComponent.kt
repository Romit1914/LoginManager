package com.yogitechnolabs.loginmanager.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogitechnolabs.loginmanager.R
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

data class StoryItem(
    val image: Any,
    val title: String
)

class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val recyclerView: RecyclerView
    private var aspectRatio: Float = 1f // Default 1:1
    private var spanCount = 1
    private var isHorizontal = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
        recyclerView = findViewById(R.id.storyRecyclerView)

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.StoryListComponent)
            spanCount = a.getInt(R.styleable.StoryListComponent_spanCount, 1)
            isHorizontal = a.getInt(R.styleable.StoryListComponent_orientation, 0) == 1
            val ratioText = a.getString(R.styleable.StoryListComponent_aspectRatio) ?: "1:1"
            aspectRatio = parseAspectRatio(ratioText)
            a.recycle()
        }
    }

    private fun parseAspectRatio(ratio: String): Float {
        return try {
            val parts = ratio.split(":")
            if (parts.size == 2) parts[0].toFloat() / parts[1].toFloat() else 1f
        } catch (e: Exception) {
            1f
        }
    }

    fun setStories(stories: List<StoryItem>) {
        recyclerView.adapter = StoryAdapter(context, stories, aspectRatio)
        recyclerView.layoutManager = GridLayoutManager(
            context,
            spanCount,
            if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            false
        )
    }

    private class StoryAdapter(
        private val context: Context,
        private val stories: List<StoryItem>,
        private val aspectRatio: Float
    ) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

        inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.storyImage)
            val textView: TextView = itemView.findViewById(R.id.storyTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.cmp_list_view, parent, false)
            return StoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
            val item = stories[position]
            Glide.with(context).load(item.image).into(holder.imageView)
            holder.textView.text = item.title

            // 🔹 Apply aspect ratio dynamically
            holder.imageView.post {
                val width = holder.imageView.width
                holder.imageView.layoutParams.height = (width / aspectRatio).toInt()
                holder.imageView.requestLayout()
            }
        }

        override fun getItemCount(): Int = stories.size
    }
}
