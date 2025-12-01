package com.yogitechnolabs.loginmanager.saloonapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.databinding.ItemServiceBinding
import com.yogitechnolabs.loginmanager.saloonapp.data.Service

class ServiceAdapter : RecyclerView.Adapter<ServiceAdapter.Holder>() {

    private val list = mutableListOf<Service>()

    fun setData(data: List<Service>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    class Holder(val bind: ItemServiceBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val service = list[position]
        holder.bind.txtServiceName.text = service.serviceName
        holder.bind.txtPrice.text = "₹ ${service.price}"
    }

    override fun getItemCount() = list.size
}