package com.yogitechnolabs.loginmanager.saloonapp.employee

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.yogitechnolabs.loginmanager.R

@SuppressLint("MissingInflatedId")
fun showEmployeeForm(
    context: Context,
    rootView: ViewGroup,
    employee: Employee? = null,
    onSave: (Employee) -> Unit
) {
    val view = LayoutInflater.from(context)
        .inflate(R.layout.employee_form_screen, rootView, false)

    rootView.removeAllViews()
    rootView.addView(view)

    val etName = view.findViewById<EditText>(R.id.etName)
    val etPhone = view.findViewById<EditText>(R.id.etPhone)
    val etRole = view.findViewById<EditText>(R.id.etRole)
    val btnSave = view.findViewById<Button>(R.id.btnSave)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)

    // -------- Edit Mode --------
    if (employee != null) {
        etName.setText(employee.name)
        etPhone.setText(employee.phone)
        etRole.setText(employee.role)
    }

    btnSave.setOnClickListener {

        val newEmployee =
            employee?.copy(
                name = etName.text.toString(),
                phone = etPhone.text.toString(),
                role = etRole.text.toString()
            ) ?: Employee(
                id = System.currentTimeMillis().toString(),
                name = etName.text.toString(),
                phone = etPhone.text.toString(),
                role = etRole.text.toString()
            )

        onSave(newEmployee)
    }

    btnCancel.setOnClickListener {
        // Go back to Employee Manager
        showEmployeeManager(
            context,
            rootView,
            employees = mutableListOf(), // YOU WILL PASS ORIGINAL LIST HERE
            onAdd = {},
            onUpdate = {},
            onDelete = {}
        )
    }
}
