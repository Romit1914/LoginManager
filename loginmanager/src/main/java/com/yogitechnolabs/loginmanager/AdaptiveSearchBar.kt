package com.yogitechnolabs.loginmanager

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdaptiveSearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val etSearch: EditText
    private val ivClear: ImageView
    private val ivVoice: ImageView
    private val rvSuggestions: RecyclerView

    private var suggestionsAdapter: SuggestionsAdapter

    private var onSearchQueryChanged: ((String) -> Unit)? = null
    private var onVoiceSearchClicked: (() -> Unit)? = null
    private var onSuggestionClicked: ((String) -> Unit)? = null

    private val debouncePeriod = 300L
    private var debounceJob: Job? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_adaptive_search_bar, this, true)

        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        ivVoice = findViewById(R.id.ivVoice)
        rvSuggestions = findViewById(R.id.rvSuggestions)

        rvSuggestions.layoutManager = LinearLayoutManager(context)
        suggestionsAdapter = SuggestionsAdapter { suggestion ->
            etSearch.setText(suggestion)
            etSearch.setSelection(suggestion.length)
            hideSuggestions()
            onSuggestionClicked?.invoke(suggestion)
        }
        rvSuggestions.adapter = suggestionsAdapter

        setupListeners()
    }

    private fun setupListeners() {
        ivClear.setOnClickListener {
            etSearch.text.clear()
            hideSuggestions()
        }

        ivVoice.setOnClickListener {
            onVoiceSearchClicked?.invoke()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                if (query.isNotEmpty()) {
                    debounceJob?.cancel()
                    debounceJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(debouncePeriod)
                        onSearchQueryChanged?.invoke(query)
                    }
                    showSuggestions()
                } else {
                    hideSuggestions()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun setSuggestions(list: List<String>) {
        suggestionsAdapter.submitList(list)
        if (list.isNotEmpty()) {
            showSuggestions()
        } else {
            hideSuggestions()
        }
    }

    fun setOnSearchQueryChangedListener(listener: (String) -> Unit) {
        onSearchQueryChanged = listener
    }

    fun setOnVoiceSearchClickListener(listener: () -> Unit) {
        onVoiceSearchClicked = listener
    }

    fun setOnSuggestionClickListener(listener: (String) -> Unit) {
        onSuggestionClicked = listener
    }

    private fun showSuggestions() {
        rvSuggestions.visibility = View.VISIBLE
    }

    private fun hideSuggestions() {
        rvSuggestions.visibility = View.GONE
    }

    fun clearSearch() {
        etSearch.text.clear()
        hideSuggestions()
    }
}
