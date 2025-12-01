package com.yogitechnolabs.loginmanager.saloonapp.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R

class EmployeeAdapter(
    private val employees: List<Employee>,
    private val onEdit: (Employee) -> Unit,
    private val onDelete: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee_admin, parent, false)
        return EmployeeVH(v)
    }

    override fun onBindViewHolder(holder: EmployeeVH, position: Int) {
        val emp = employees[position]
        holder.bind(emp)

        holder.itemView.findViewById<ImageView>(R.id.btnEdit).setOnClickListener {
            onEdit(emp)
        }

        holder.itemView.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            onDelete(emp)
        }
    }

    override fun getItemCount() = employees.size
}

class EmployeeVH(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(emp: Employee) {
        itemView.findViewById<TextView>(R.id.tvName).text = emp.name
        itemView.findViewById<TextView>(R.id.tvPhone).text = emp.phone
        itemView.findViewById<TextView>(R.id.tvRole).text = emp.role

        // --- NEW: Show services with prices ---
        val tvServices = itemView.findViewById<TextView>(R.id.tvServices)
        if (emp.services.isNotEmpty()) {
            val servicesText = emp.services.joinToString(separator = "\n") {
                "${it.serviceName}: ₹${it.price}"
            }
            tvServices.text = servicesText
        } else {
            tvServices.text = "No services added"
        }

        // Optionally, total price
        val tvTotal = itemView.findViewById<TextView>(R.id.tvTotalPrice)
        val total = emp.services.sumOf { it.price.toIntOrNull() ?: 0 }
        tvTotal.text = "Total: ₹$total"
    }
}

