package com.yogitechnolabs.loginmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Item(val title: String, val subtitle: String) // Your data model

class RealDataAdapter(private val items: List<Item>) : RecyclerView.Adapter<RealDataAdapter.RealViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_real, parent, false)
        return RealViewHolder(view)
    }

    override fun onBindViewHolder(holder: RealViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.subtitleText.text = item.subtitle
    }

    override fun getItemCount() = items.size

    class RealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subtitleText: TextView = itemView.findViewById(R.id.subtitleText)
    }
}
