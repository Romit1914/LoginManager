package com.yogitechnolabs.loginmanager.saloonapp.service

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.saloonapp.employee.ServiceItem

class ServiceAdapter(
    private val services: MutableList<ServiceItem>,
    private val onEdit: (ServiceItem) -> Unit,
    private val onDelete: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceVH>() {

    inner class ServiceVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvInfo = view.findViewById<TextView>(R.id.tvServiceInfo)
        val btnEdit = view.findViewById<ImageView>(R.id.btnEditService)
        val btnDelete = view.findViewById<ImageView>(R.id.btnDeleteService)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceVH(v)
    }

    override fun onBindViewHolder(holder: ServiceVH, position: Int) {
        val item = services[position]

        holder.tvInfo.text = "${item.serviceName}: ₹${item.price}"

        holder.btnEdit.setOnClickListener {
            onEdit(item)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount() = services.size
}
