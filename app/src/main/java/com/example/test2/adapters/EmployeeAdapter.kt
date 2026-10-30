package com.example.test2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.databinding.ItemEmployeeBinding
import com.example.test2.entities.Employee
import java.text.DecimalFormat

class EmployeeAdapter(
    private var employees: List<Employee>,
    private val onItemClick: (Employee) -> Unit,
    private val onItemLongClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private val decimalFormat = DecimalFormat("#,### đ/giờ")

    fun updateData(newEmployees: List<Employee>) {
        employees = newEmployees
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position])
    }

    override fun getItemCount(): Int = employees.size

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.tvCodeName.text = "${employee.code} - ${employee.fullname}"
            binding.tvContact.text = "${employee.email} · ${employee.phone}"
            binding.tvHourlyRate.text = decimalFormat.format(employee.hourlyRate)

            binding.root.setOnClickListener {
                onItemClick(employee)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(employee)
                true
            }
        }
    }
}
