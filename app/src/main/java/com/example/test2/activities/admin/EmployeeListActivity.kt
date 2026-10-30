package com.example.test2.activities.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.adapters.EmployeeAdapter
import com.example.test2.dal.AppDB
import com.example.test2.databinding.ActivityEmployeeListBinding
import com.example.test2.entities.Employee
import kotlinx.coroutines.launch

class EmployeeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeListBinding
    private lateinit var adapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, EmployeeFormActivity::class.java))
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            loadEmployees(text?.toString()?.trim() ?: "")
        }
    }

    override fun onResume() {
        super.onResume()
        val query = binding.edtSearch.text.toString().trim()
        loadEmployees(query)
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(
            employees = emptyList(),
            onItemClick = { employee ->
                val intent = Intent(this, EmployeeFormActivity::class.java).apply {
                    putExtra("EMPLOYEE_ID", employee.id)
                }
                startActivity(intent)
            },
            onItemLongClick = { employee ->
                showDeleteConfirmDialog(employee)
            }
        )
        binding.rvEmployees.layoutManager = LinearLayoutManager(this)
        binding.rvEmployees.adapter = adapter
    }

    private fun loadEmployees(query: String) {
        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            val list = if (query.isEmpty()) {
                dao.getAllEmployees()
            } else {
                dao.searchEmployees(query)
            }
            adapter.updateData(list)
        }
    }

    private fun showDeleteConfirmDialog(employee: Employee) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Xóa nhân viên ${employee.code} - ${employee.fullname}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteEmployee(employee)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteEmployee(employee: Employee) {
        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            dao.deleteEmployeeFull(employee)
            Toast.makeText(this@EmployeeListActivity, "Xóa nhân viên thành công", Toast.LENGTH_SHORT).show()
            loadEmployees(binding.edtSearch.text.toString().trim())
        }
    }
}
