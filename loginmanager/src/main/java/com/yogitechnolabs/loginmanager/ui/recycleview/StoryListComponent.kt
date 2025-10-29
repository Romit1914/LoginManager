package com.yogitechnolabs.loginmanager.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogitechnolabs.loginmanager.R

data class StoryItem(
    val image: Any,    // URL or drawable resource
    val title: String
)

/** 🔹 A reusable RecyclerView component with XML-controllable dimensions */
class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val recyclerView: RecyclerView
    private var spanCount: Int = 1
    private var isHorizontal: Boolean = false
    private var itemWidth: Int? = null
    private var itemHeight: Int? = null
    private var dimensionRatio: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
        recyclerView = findViewById(R.id.storyRecyclerView)

        // 🔸 Read XML attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.StoryListComponent, 0, 0).apply {
            try {
                spanCount = getInt(R.styleable.StoryListComponent_spanCount, 1)
                isHorizontal = getInt(R.styleable.StoryListComponent_orientation, 0) == 1
                itemWidth = getDimensionPixelSize(R.styleable.StoryListComponent_itemWidth, -1)
                    .takeIf { it > 0 }
                itemHeight = getDimensionPixelSize(R.styleable.StoryListComponent_itemHeight, -1)
                    .takeIf { it > 0 }
                dimensionRatio = getString(R.styleable.StoryListComponent_dimensionRatio)
            } finally {
                recycle()
            }
        }
    }

    /** 🔹 Set story data dynamically */
    fun setStories(stories: List<StoryItem>) {
        recyclerView.layoutManager = GridLayoutManager(
            context,
            spanCount,
            if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            false
        )
        recyclerView.adapter = StoryAdapter(
            context,
            stories,
            itemWidth,
            itemHeight,
            dimensionRatio
        )
    }

    /** 🔹 Internal Adapter */
    private class StoryAdapter(
        private val context: Context,
        private val stories: List<StoryItem>,
        private val itemWidth: Int?,
        private val itemHeight: Int?,
        private val dimensionRatio: String?
    ) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

        inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.storyImage)
            val textView: TextView = itemView.findViewById(R.id.storyTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.cmp_list_view, parent, false)

            // 🔹 Apply custom size if provided
            val params = view.layoutParams
            itemWidth?.let { params.width = it }
            itemHeight?.let { params.height = it }

            // 🔹 Apply ratio
            dimensionRatio?.let { ratio ->
                view.post {
                    val parts = ratio.split(":")
                    if (parts.size == 2) {
                        val w = parts[0].toFloatOrNull() ?: 1f
                        val h = parts[1].toFloatOrNull() ?: 1f
                        val width = view.width
                        if (width > 0) {
                            params.height = (width * (h / w)).toInt()
                            view.layoutParams = params
                        }
                    }
                }
            }

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
