package com.example.test2.activities.admin

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test2.dal.AppDB
import com.example.test2.databinding.ActivityEmployeeFormBinding
import com.example.test2.entities.Account
import com.example.test2.entities.Employee
import kotlinx.coroutines.launch

class EmployeeFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeFormBinding
    private var employeeId: Long = -1L
    private var currentEmployee: Employee? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        employeeId = intent.getLongExtra("EMPLOYEE_ID", -1L)

        if (employeeId != -1L) {
            binding.tvTitle.text = "Sửa nhân viên"
            binding.edtCode.isEnabled = false
            loadEmployeeData()
        } else {
            binding.tvTitle.text = "Thêm nhân viên"
            binding.edtCode.isEnabled = true
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            saveEmployee()
        }
    }

    private fun loadEmployeeData() {
        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            val employee = dao.getEmployeeById(employeeId)
            if (employee != null) {
                currentEmployee = employee
                binding.edtCode.setText(employee.code)
                binding.edtFullname.setText(employee.fullname)
                binding.edtEmail.setText(employee.email)
                binding.edtPhone.setText(employee.phone)
                binding.edtHourlyRate.setText(
                    if (employee.hourlyRate % 1.0 == 0.0) {
                        employee.hourlyRate.toLong().toString()
                    } else {
                        employee.hourlyRate.toString()
                    }
                )
            }
        }
    }

    private fun saveEmployee() {
        val code = binding.edtCode.text.toString().trim()
        val fullname = binding.edtFullname.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val rateStr = binding.edtHourlyRate.text.toString().trim()

        var isValid = true

        if (code.isEmpty()) {
            binding.edtCode.error = "Vui lòng nhập mã nhân viên"
            isValid = false
        }

        if (fullname.isEmpty()) {
            binding.edtFullname.error = "Vui lòng nhập họ và tên"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.edtEmail.error = "Vui lòng nhập email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Email không đúng định dạng"
            isValid = false
        }

        if (phone.isEmpty()) {
            binding.edtPhone.error = "Vui lòng nhập số điện thoại"
            isValid = false
        }

        val hourlyRate = rateStr.toDoubleOrNull()
        if (rateStr.isEmpty()) {
            binding.edtHourlyRate.error = "Vui lòng nhập mức lương theo giờ"
            isValid = false
        } else if (hourlyRate == null || hourlyRate <= 0) {
            binding.edtHourlyRate.error = "Mức lương theo giờ phải lớn hơn 0"
            isValid = false
        }

        if (!isValid) return

        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()

            if (employeeId == -1L) {
                // Thêm nhân viên
                val existingCode = dao.getEmployeeByCode(code)
                if (existingCode != null) {
                    binding.edtCode.error = "Mã nhân viên đã tồn tại"
                    return@launch
                }

                val existingEmail = dao.getEmployeeByEmail(email)
                if (existingEmail != null) {
                    binding.edtEmail.error = "Email đã tồn tại"
                    return@launch
                }

                // Tạo Account cho nhân viên (username = code, password = "123", role = "EMPLOYEE")
                val account = Account(
                    username = code,
                    password = "123",
                    role = "EMPLOYEE"
                )
                val accountId = dao.insertAccount(account)

                val newEmployee = Employee(
                    code = code,
                    fullname = fullname,
                    email = email,
                    phone = phone,
                    hourlyRate = hourlyRate!!,
                    accountId = accountId
                )
                dao.insertEmployee(newEmployee)

            } else {
                // Sửa nhân viên
                val emp = currentEmployee ?: return@launch

                if (emp.email != email) {
                    val existingEmail = dao.getEmployeeByEmail(email)
                    if (existingEmail != null && existingEmail.id != emp.id) {
                        binding.edtEmail.error = "Email đã tồn tại"
                        return@launch
                    }
                }

                val updatedEmployee = emp.copy(
                    fullname = fullname,
                    email = email,
                    phone = phone,
                    hourlyRate = hourlyRate!!
                )
                dao.updateEmployee(updatedEmployee)
            }

            Toast.makeText(this@EmployeeFormActivity, "Lưu thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
