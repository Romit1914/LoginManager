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
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogitechnolabs.loginmanager.ui.ButtonView

data class StoryItem(
    val image: Any,
    val title: String
)

data class QuizQuestion(
    val question: String,
    val options: List<String>
)

class StoryListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var layoutType: String = "story" // default
    private var currentView: View? = null

    // RecyclerView
    private var recyclerView: RecyclerView? = null
    private var aspectRatio: Float = 1f
    private var spanCount = 1
    private var isHorizontal = false

    init {
        orientation = VERTICAL
        setupLayout("story") // default
    }

    /** 🔹 Switch between story / quiz layout */
    fun setLayoutType(type: String) {
        setupLayout(type)
    }

    private fun setupLayout(type: String) {
        removeAllViews()
        layoutType = type

        // Common layout — just RecyclerView
        currentView = LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
        recyclerView = currentView!!.findViewById(R.id.storyRecyclerView)
    }

    /** 🔹 Story List */
    fun setStories(stories: List<StoryItem>) {
        if (layoutType != "story" || recyclerView == null) return

        recyclerView!!.layoutManager = GridLayoutManager(
            context,
            spanCount,
            if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            false
        )
        recyclerView!!.adapter = StoryAdapter(context, stories, aspectRatio)
    }

    /** 🔹 Quiz Questions */
    fun setQuestions(questions: List<QuizQuestion>) {
        if (layoutType != "quiz" || recyclerView == null) return

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = QuizAdapter(context, questions)
    }

    // ============================================================
    // 🔹 Story Adapter
    // ============================================================
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

    // ============================================================
    // 🔹 Quiz Adapter (multiple questions)
    // ============================================================
    private class QuizAdapter(
        private val context: Context,
        private val questions: List<QuizQuestion>
    ) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

        inner class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val questionText: TextView = view.findViewById(R.id.storyTitle)
            val imageView: ImageView = view.findViewById(R.id.storyImage)
            val optionContainer: LinearLayout = view.findViewById(R.id.optionContainer)
            val submitButton: ButtonView = view.findViewById(R.id.submitButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
            // Reusing cmp_list_view.xml but with dynamic options
            val view = LayoutInflater.from(context).inflate(R.layout.cmp_list_view, parent, false)

            // Add new dynamic containers inside
            val optionContainer = LinearLayout(context)
            optionContainer.orientation = LinearLayout.VERTICAL
            optionContainer.id = R.id.optionContainer
            (view as ViewGroup).addView(optionContainer)

            val submit = ButtonView(context)
            submit.setText("Submit")
            submit.id = R.id.submitButton
            view.addView(submit)

            return QuizViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            val q = questions[position]
            holder.questionText.text = "${position + 1}. ${q.question}"
            holder.imageView.visibility = View.GONE // hide story image for quiz

            holder.optionContainer.removeAllViews()
            for (opt in q.options) {
                val cb = CheckBox(context)
                cb.text = opt
                holder.optionContainer.addView(cb)
            }

            holder.submitButton.setOnClickListener {
                val selected = mutableListOf<String>()
                for (i in 0 until holder.optionContainer.childCount) {
                    val cb = holder.optionContainer.getChildAt(i) as CheckBox
                    if (cb.isChecked) selected.add(cb.text.toString())
                }
                Toast.makeText(context, "Q${position + 1}: $selected", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = questions.size
    }
}
