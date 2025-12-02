package com.yogitechnolabs.loginmanager.saloonapp.employee

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.saloonapp.service.ServiceAdapter

@SuppressLint("MissingInflatedId")
fun showEmployeeForm(
    context: Context,
    rootView: ViewGroup,
    employee: Employee?,
    onSubmit: (Employee) -> Unit
) {
    val view = LayoutInflater.from(context)
        .inflate(R.layout.employee_form_screen, rootView, false)

    rootView.removeAllViews()
    rootView.addView(view)

    val etName = view.findViewById<EditText>(R.id.etName)
    val etPhone = view.findViewById<EditText>(R.id.etPhone)
    val etRole = view.findViewById<EditText>(R.id.etRole)

    val rvServices = view.findViewById<RecyclerView>(R.id.rvServices)
    val btnAddService = view.findViewById<Button>(R.id.btnAddService)
    val btnSave = view.findViewById<Button>(R.id.btnSaveEmployee)

    val servicesList = employee?.services ?: mutableListOf()

    if (employee != null) {
        etName.setText(employee.name)
        etPhone.setText(employee.phone)
        etRole.setText(employee.role)
    }

    // ⬅️ Adapter को var बनाना जरूरी है (fixes unresolved reference)
    var adapter: ServiceAdapter? = null

    adapter = ServiceAdapter(
        servicesList,
        onEdit = { serviceItem ->
            showServiceEditDialog(context, serviceItem) {
                adapter?.notifyDataSetChanged()
            }
        },
        onDelete = { serviceItem ->
            servicesList.remove(serviceItem)
            adapter?.notifyDataSetChanged()
        }
    )

    rvServices.layoutManager = LinearLayoutManager(context)
    rvServices.adapter = adapter

    btnAddService.setOnClickListener {
        showAddServiceDialog(context) { newService ->
            servicesList.add(newService)
            adapter?.notifyDataSetChanged()
        }
    }

    btnSave.setOnClickListener {
        val emp = employee ?: Employee(
            id = System.currentTimeMillis().toString(),
            name = "",
            phone = "",
            role = "",
            services = servicesList
        )

        emp.name = etName.text.toString()
        emp.phone = etPhone.text.toString()
        emp.role = etRole.text.toString()
        emp.services = servicesList

        onSubmit(emp)
    }
}


private fun showServiceEditDialog(
    context: Context,
    service: ServiceItem,
    onSave: () -> Unit
) {
    val dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_add_service, null)

    val etName = dialogView.findViewById<EditText>(R.id.etServiceName)
    val etPrice = dialogView.findViewById<EditText>(R.id.etServicePrice)

    etName.setText(service.serviceName)
    etPrice.setText(service.price.toString())

    val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Edit Service")
        .setView(dialogView)
        .setPositiveButton("Save") { _, _ ->
            service.serviceName = etName.text.toString()
            service.price = etPrice.text.toString()
            onSave()
        }
        .setNegativeButton("Cancel", null)
        .create()

    dialog.show()
}


private fun showAddServiceDialog(
    context: Context,
    onSave: (ServiceItem) -> Unit
) {
    val dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_add_service, null)

    val etName = dialogView.findViewById<EditText>(R.id.etServiceName)
    val etPrice = dialogView.findViewById<EditText>(R.id.etServicePrice)

    val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Add Service")
        .setView(dialogView)
        .setPositiveButton("Add") { _, _ ->
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()

            if (name.isNotEmpty()) {
                // keep price as string (you used String in ServiceItem), or convert to int if needed
                onSave(ServiceItem(name, price))
            }
        }
        .setNegativeButton("Cancel", null)
        .create()

    dialog.show()
}

