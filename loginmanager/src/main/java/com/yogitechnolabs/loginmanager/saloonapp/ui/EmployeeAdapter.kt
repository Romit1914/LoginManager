package com.yogitechnolabs.loginmanager.saloonapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.databinding.ItemEmployeeBinding
import com.yogitechnolabs.loginmanager.saloonapp.data.Employee

class EmployeeAdapter : RecyclerView.Adapter<EmployeeAdapter.Holder>() {

    private val list = mutableListOf<Employee>()

    fun setData(data: List<Employee>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    class Holder(val bind: ItemEmployeeBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val emp = list[position]

        holder.bind.txtName.text = emp.name
        holder.bind.txtTotal.text = "₹ " + emp.services.sumOf { it.price }

//        val serviceAdapter = ServiceAdapter()
//        holder.bind.recyclerServices.adapter = serviceAdapter
//        serviceAdapter.setData(emp.services)
    }

    override fun getItemCount() = list.size
}