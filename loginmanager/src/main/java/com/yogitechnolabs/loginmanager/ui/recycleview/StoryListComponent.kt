package com.yogitechnolabs.loginmanager.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.ui.ButtonView

data class StoryItem(val image: Any, val title: String)
data class QuizQuestion(val question: String, val options: List<String>)

class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var layoutType: String = "story"
    private var recyclerView: RecyclerView? = null
    private var aspectRatio: Float = 1f
    private var spanCount: Int = 1
    private var orientationMode: Int = RecyclerView.VERTICAL

    init {
        orientation = VERTICAL

        // 🔹 Read XML attributes
        context.withStyledAttributes(attrs, R.styleable.StoryListComponent) {
            layoutType = getString(R.styleable.StoryListComponent_layoutType) ?: "story"
            spanCount = getInt(R.styleable.StoryListComponent_spanCount, 1)
            aspectRatio = getFloat(R.styleable.StoryListComponent_aspectRatio, 1f)
            val ori = getInt(R.styleable.StoryListComponent_orientation, 1)
            orientationMode = if (ori == 0) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        }

        setupLayout(layoutType)
    }

    // 🔹 Dynamically switch between story / quiz
    fun setLayoutType(type: String) {
        setupLayout(type)
    }

    private fun setupLayout(type: String) {
        removeAllViews()
        layoutType = type

        val view = LayoutInflater.from(context)
            .inflate(R.layout.view_story_list_component, this, true)
        recyclerView = view.findViewById(R.id.storyRecyclerView)

        applyLayoutManager()
    }

    private fun applyLayoutManager() {
        recyclerView ?: return
        recyclerView!!.layoutManager =
            if (layoutType == "story") {
                GridLayoutManager(context, spanCount, orientationMode, false)
            } else {
                LinearLayoutManager(context, orientationMode, false)
            }
    }

    // 🔹 Public setters
    fun setSpanCount(count: Int) { spanCount = count; applyLayoutManager() }
    fun setAspectRatio(ratio: Float) { aspectRatio = ratio; recyclerView?.adapter?.notifyDataSetChanged() }
    fun setOrientation(horizontal: Boolean) {
        orientationMode = if (horizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        applyLayoutManager()
    }

    // 🔹 Story data
    fun setStories(stories: List<StoryItem>) {
        if (layoutType != "story" || recyclerView == null) return
        recyclerView!!.adapter = StoryAdapter(context, stories, aspectRatio)
    }

    // 🔹 Quiz data
    fun setQuestions(questions: List<QuizQuestion>) {
        if (layoutType != "quiz" || recyclerView == null) setupLayout("quiz")
        recyclerView!!.adapter = QuizAdapter(context, questions)
    }

    // ============================================================
    // Story Adapter
    // ============================================================
    private class StoryAdapter(
        private val context: Context,
        private val stories: List<StoryItem>,
        private val aspectRatio: Float
    ) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

        inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.storyImage)
            val title: TextView = view.findViewById(R.id.storyTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.cmp_list_view, parent, false)
            return StoryViewHolder(v)
        }

        override fun onBindViewHolder(h: StoryViewHolder, pos: Int) {
            val item = stories[pos]
            Glide.with(context).load(item.image).into(h.img)
            h.title.text = item.title

            h.img.post {
                val width = h.img.width
                if (width > 0) {
                    h.img.layoutParams.height = (width / aspectRatio).toInt()
                    h.img.requestLayout()
                }
            }
        }

        override fun getItemCount() = stories.size
    }

    // ============================================================
    // Quiz Adapter
    // ============================================================
    private class QuizAdapter(
        private val context: Context,
        private val questions: List<QuizQuestion>
    ) : RecyclerView.Adapter<QuizAdapter.QuizVH>() {

        inner class QuizVH(view: View) : RecyclerView.ViewHolder(view) {
            val question: TextView = view.findViewById(R.id.tvQuestion)
            val options: LinearLayout = view.findViewById(R.id.optionContainer)
            val submit: ButtonView = view.findViewById(R.id.submitButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizVH {
            val v = LayoutInflater.from(context).inflate(R.layout.view_quiz_component, parent, false)
            return QuizVH(v)
        }

        override fun onBindViewHolder(h: QuizVH, pos: Int) {
            val q = questions[pos]
            h.question.text = "${pos + 1}. ${q.question}"
            h.options.removeAllViews()

            for (opt in q.options) {
                val cb = CheckBox(context).apply { text = opt }
                h.options.addView(cb)
            }

            h.submit.setOnClickListener {
                val selected = mutableListOf<String>()
                for (i in 0 until h.options.childCount) {
                    val v = h.options.getChildAt(i)
                    if (v is CheckBox && v.isChecked) selected.add(v.text.toString())
                }
                Toast.makeText(context, "Q${pos + 1}: $selected", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount() = questions.size
    }
}
