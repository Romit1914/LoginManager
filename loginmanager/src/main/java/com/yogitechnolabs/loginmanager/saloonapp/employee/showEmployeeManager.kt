package com.yogitechnolabs.loginmanager.saloonapp.employee

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R

@SuppressLint("NotifyDataSetChanged")
fun showEmployeeManager(
    context: Context,
    rootView: ViewGroup,
    employees: MutableList<Employee>,
    onAdd: (Employee) -> Unit,
    onUpdate: (Employee) -> Unit,
    onDelete: (Employee) -> Unit
) {
    val view = LayoutInflater.from(context)
        .inflate(R.layout.employee_manager_screen, rootView, false)

    rootView.removeAllViews()
    rootView.addView(view)

    val rv = view.findViewById<RecyclerView>(R.id.rvEmployees)
    val btnAdd = view.findViewById<Button>(R.id.btnAddEmployee)

    rv.layoutManager = LinearLayoutManager(context)
    rv.adapter = EmployeeAdapter(
        employees,
        onEdit = { emp ->
            showEmployeeForm(context, rootView, emp) { updated ->
                onUpdate(updated)      // ONLY CALLBACK
            }
        },
        onDelete = { emp ->
            onDelete(emp)            // ONLY CALLBACK
        }
    )

    btnAdd.setOnClickListener {
        showEmployeeForm(context, rootView, null) { newEmp ->
            onAdd(newEmp)            // ONLY CALLBACK
        }
    }
}
