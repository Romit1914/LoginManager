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
    val image: Any,    // URL or drawable resource
    val title: String
)

/** 🔹 A reusable RecyclerView component to show list/grid of story items */
class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val recyclerView: RecyclerView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
        recyclerView = findViewById(R.id.storyRecyclerView)
    }

    /** Set story data dynamically */
    fun setStories(stories: List<StoryItem>, spanCount: Int = 1, horizontal: Boolean = false) {
        recyclerView.adapter = StoryAdapter(context, stories)
        recyclerView.layoutManager = GridLayoutManager(
            context,
            spanCount,
            if (horizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            false
        )
    }

    /** Adapter for story list */
    private class StoryAdapter(
        private val context: Context,
        private val stories: List<StoryItem>
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
        }

        override fun getItemCount(): Int = stories.size
    }
}
