package com.yogitechnolabs.loginmanager.ui.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R

class ListComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private var itemLayoutResId: Int = 0
    private var orientation: Int = RecyclerView.VERTICAL
    private var spanCount: Int = 1

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_story_list_component, this, true)
        recyclerView = view.findViewById(R.id.recyclerView)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponent, 0, 0).apply {
            try {
                itemLayoutResId = getResourceId(R.styleable.ListComponent_itemLayout, 0)
                orientation = getInt(R.styleable.ListComponent_orientation, 0)
                spanCount = getInt(R.styleable.ListComponent_spanCount, 1)
            } finally {
                recycle()
            }
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = if (spanCount > 1) {
            GridLayoutManager(context, spanCount)
        } else {
            LinearLayoutManager(
                context,
                if (orientation == 1) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
                false
            )
        }
        recyclerView.layoutManager = layoutManager
    }

    /** ✅ Developer agar programmatically layout set karna chahe */
    fun setItemLayout(layoutResId: Int) {
        this.itemLayoutResId = layoutResId
    }

    /**
     * ✅ Universal reusable setup:
     * Developer apna data, bindView aur optional onClick listener de sakta hai.
     */
    fun <T> setItems(
        items: List<T>,
        bindView: (View, T, Int) -> Unit,
        onItemClick: ((T, Int) -> Unit)? = null  // 👈 optional click listener
    ) {
        recyclerView.adapter = object : RecyclerView.Adapter<CustomViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
                val layoutToInflate = if (itemLayoutResId != 0) {
                    itemLayoutResId
                } else {
                    // ⚠️ fallback safe default layout
                    R.layout.view_story_list_component
                }
                val view = LayoutInflater.from(parent.context).inflate(layoutToInflate, parent, false)
                return CustomViewHolder(view)
            }

            override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
                val item = items[position]
                bindView(holder.itemView, item, position)

                // 👇 Optional click listener (only if provided)
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(item, position)
                }
            }

            override fun getItemCount(): Int = items.size
        }
    }

    private class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
