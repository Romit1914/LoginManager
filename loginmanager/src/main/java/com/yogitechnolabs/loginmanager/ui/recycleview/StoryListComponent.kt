package com.yogitechnolabs.loginmanager.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
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
    private var aspectRatio: Float = 1f
    private var spanCount: Int = 1
    private var orientationMode: Int = RecyclerView.VERTICAL
    private var customItemLayout: Int = 0  // 👈 Developer custom layout

    init {
        orientation = VERTICAL

        context.withStyledAttributes(attrs, R.styleable.ListComponent) {
            layoutType = getString(R.styleable.ListComponent_layoutType) ?: "story"
            spanCount = getInt(R.styleable.ListComponent_spanCount, 1)
            val ratioStr = getString(R.styleable.ListComponent_aspectRatio) ?: "1:1"
            aspectRatio = parseAspectRatio(ratioStr)
            val ori = getInt(R.styleable.ListComponent_orientation, 0)
            orientationMode = if (ori == 1) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
            customItemLayout = getResourceId(R.styleable.ListComponent_itemLayout, 0)
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
        recyclerView = view.findViewById(R.id.recyclerView)

        applyLayoutManager()
    }

    private fun applyLayoutManager() {
        recyclerView?.layoutManager =
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

    fun setCustomItemLayout(@LayoutRes layoutRes: Int) {
        customItemLayout = layoutRes
    }

    // ============================================================
    // Default Story Setup
    // ============================================================
    fun setStories(stories: List<StoryItem>) {
        recyclerView?.adapter =
            StoryAdapter(context, stories, aspectRatio, customItemLayout)
    }

    // ============================================================
    // Default Quiz Setup
    // ============================================================
    fun setQuestions(questions: List<QuizQuestion>) {
        recyclerView?.adapter = QuizAdapter(context, questions)
    }

    // ============================================================
    // 🚀 Universal Generic Setup for Developer Custom Model + Layout
    // ============================================================
    fun <T> setCustomData(
        items: List<T>,
        @LayoutRes layoutRes: Int,
        onBind: (view: View, item: T, position: Int) -> Unit
    ) {
        recyclerView?.adapter = object : RecyclerView.Adapter<GenericVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericVH {
                val v = LayoutInflater.from(context).inflate(layoutRes, parent, false)
                return GenericVH(v)
            }

            override fun onBindViewHolder(holder: GenericVH, position: Int) {
                onBind(holder.itemView, items[position], position)
            }

            override fun getItemCount() = items.size
        }
    }

    private class GenericVH(view: View) : RecyclerView.ViewHolder(view)

    // ============================================================
    // Story Adapter (Supports Developer’s Custom Layout)
    // ============================================================
    private class StoryAdapter(
        private val context: Context,
        private val stories: List<StoryItem>,
        private val aspectRatio: Float,
        private val customLayout: Int
    ) : RecyclerView.Adapter<StoryAdapter.StoryVH>() {

        inner class StoryVH(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView? = view.findViewById(R.id.storyImage)
            val title: TextView? = view.findViewById(R.id.storyTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryVH {
            val layoutToInflate = if (customLayout != 0) customLayout else R.layout.cmp_list_view
            val v = LayoutInflater.from(context).inflate(layoutToInflate, parent, false)
            return StoryVH(v)
        }

        override fun onBindViewHolder(h: StoryVH, pos: Int) {
            val item = stories[pos]
            h.title?.apply {
                text = item.title ?: ""
                visibility = if (item.title.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            h.img?.apply {
                visibility = if (item.image != null) View.VISIBLE else View.GONE
                Glide.with(context).load(item.image).into(this)

                post {
                    val width = width
                    if (width > 0 && aspectRatio > 0) {
                        val params = layoutParams
                        params.height = (width / aspectRatio).toInt()
                        layoutParams = params
                    }
                }
            }
        }

        override fun getItemCount() = stories.size
    }

    // ============================================================
    // Quiz Adapter (Same as before)
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
            val v = LayoutInflater.from(context)
                .inflate(R.layout.view_quiz_component, parent, false)
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
