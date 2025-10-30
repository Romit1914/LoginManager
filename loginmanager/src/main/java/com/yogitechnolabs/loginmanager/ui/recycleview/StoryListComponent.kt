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

data class StoryItem(val image: Any? = null, val title: String? = null)
data class QuizQuestion(val question: String, val options: List<String>)

class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var layoutType: String = "story"
    private var recyclerView: RecyclerView? = null
    private var aspectRatio: Float = 1f // ✅ Default 1:1 ratio
    private var spanCount: Int = 1
    private var orientationMode: Int = RecyclerView.VERTICAL

    init {
        orientation = VERTICAL

        context.withStyledAttributes(attrs, R.styleable.StoryListComponent) {
            layoutType = getString(R.styleable.StoryListComponent_layoutType) ?: "story"
            spanCount = getInt(R.styleable.StoryListComponent_spanCount, 1)

            // ✅ Safe aspect ratio parsing (default 1:1)
            val ratioStr = getString(R.styleable.StoryListComponent_aspectRatio) ?: "1:1"
            aspectRatio = parseAspectRatio(ratioStr)

            // ✅ Orientation (0 = vertical, 1 = horizontal)
            val ori = getInt(R.styleable.StoryListComponent_orientation, 0)
            orientationMode = if (ori == 1) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        }

        setupLayout(layoutType)
    }

    private fun parseAspectRatio(value: String): Float {
        return try {
            val parts = value.split(":")
            if (parts.size == 2) {
                val w = parts[0].toFloat()
                val h = parts[1].toFloat()
                if (w > 0 && h > 0) w / h else 1f
            } else 1f
        } catch (e: Exception) {
            1f
        }
    }

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

    fun setSpanCount(count: Int) {
        spanCount = count
        applyLayoutManager()
    }

    fun setAspectRatio(ratioString: String) {
        aspectRatio = parseAspectRatio(ratioString)
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    fun setOrientation(horizontal: Boolean) {
        orientationMode = if (horizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        applyLayoutManager()
    }

    // 🔹 For stories
    fun setStories(stories: List<StoryItem>) {
        if (layoutType != "story" || recyclerView == null) return
        recyclerView!!.adapter = StoryAdapter(context, stories, aspectRatio)
    }

    // 🔹 For quiz
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

            // Title
            if (!item.title.isNullOrEmpty()) {
                h.title.visibility = View.VISIBLE
                h.title.text = item.title
            } else h.title.visibility = View.GONE

            // Image
            if (item.image != null) {
                h.img.visibility = View.VISIBLE
                Glide.with(context).load(item.image).into(h.img)
                h.img.requestLayout()
            } else h.img.visibility = View.GONE

            // ✅ Aspect ratio properly applied after layout
            h.img.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?, left: Int, top: Int, right: Int, bottom: Int,
                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    h.img.removeOnLayoutChangeListener(this)
                    val width = h.img.width
                    if (width > 0 && aspectRatio > 0) {
                        val params = h.img.layoutParams
                        params.height = (width / aspectRatio).toInt()
                        h.img.layoutParams = params
                    }
                }
            })
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
