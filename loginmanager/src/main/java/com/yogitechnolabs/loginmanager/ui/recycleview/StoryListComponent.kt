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
import android.widget.CheckBox
import android.widget.Button

data class StoryItem(
    val image: Any,
    val title: String
)

class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var layoutType: String = "story" // default
    private var currentView: View? = null

    // Story Layout components
    private var recyclerView: RecyclerView? = null
    private var aspectRatio: Float = 1f
    private var spanCount = 1
    private var isHorizontal = false

    // Quiz Layout components
    private var questionText: TextView? = null
    private var option1: CheckBox? = null
    private var option2: CheckBox? = null
    private var option3: CheckBox? = null
    private var submitButton: Button? = null

    init {
        orientation = VERTICAL
        setupLayout("story") // default
    }

    /** 🔹 Developer calls this to switch layout dynamically */
    fun setLayoutType(type: String) {
        setupLayout(type)
    }

    /** 🔹 Internal setup for different layout types */
    private fun setupLayout(type: String) {
        removeAllViews() // remove previous layout
        layoutType = type

        when (type) {
            "story" -> {
                currentView = LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
                recyclerView = currentView!!.findViewById(R.id.storyRecyclerView)
                addView(currentView)
            }

            "quiz" -> {
                currentView = LayoutInflater.from(context).inflate(R.layout.view_quiz_component, this, false)
                questionText = currentView!!.findViewById(R.id.questionText)
                option1 = currentView!!.findViewById(R.id.option1)
                option2 = currentView!!.findViewById(R.id.option2)
                option3 = currentView!!.findViewById(R.id.option3)
                submitButton = currentView!!.findViewById(R.id.submitButton)

                submitButton?.setOnClickListener {
                    val selected = mutableListOf<String>()
                    if (option1?.isChecked == true) selected.add(option1?.text.toString())
                    if (option2?.isChecked == true) selected.add(option2?.text.toString())
                    if (option3?.isChecked == true) selected.add(option3?.text.toString())

                    android.widget.Toast.makeText(context, "Selected: $selected", android.widget.Toast.LENGTH_SHORT).show()
                }

                addView(currentView)
            }
        }
    }

    /** 🔹 Set stories (only works for story layout) */
    fun setStories(stories: List<StoryItem>) {
        if (layoutType != "story" || recyclerView == null) return

        recyclerView!!.adapter = StoryAdapter(context, stories, aspectRatio)
        recyclerView!!.layoutManager = GridLayoutManager(
            context,
            spanCount,
            if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            false
        )
    }

    /** 🔹 Set question and options (only works for quiz layout) */
    fun setQuestion(question: String, options: List<String>) {
        if (layoutType != "quiz") return
        questionText?.text = question
        option1?.text = options.getOrNull(0) ?: ""
        option2?.text = options.getOrNull(1) ?: ""
        option3?.text = options.getOrNull(2) ?: ""
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

            holder.imageView.post {
                val width = holder.imageView.width
                holder.imageView.layoutParams.height = (width / aspectRatio).toInt()
                holder.imageView.requestLayout()
            }
        }

        override fun getItemCount(): Int = stories.size
    }
}
