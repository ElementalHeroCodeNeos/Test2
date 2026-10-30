package com.example.test2.activities.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test2.activities.LoginActivity
import com.example.test2.dal.AppDB
import com.example.test2.databinding.ActivityEmployeeHomeBinding
import com.example.test2.entities.Employee
import com.example.test2.entities.WorkLog
import com.example.test2.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeHomeBinding
    private lateinit var sessionManager: SessionManager
    private var currentEmployee: Employee? = null
    private var currentTodayLog: WorkLog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnCheck.setOnClickListener {
            handleCheckAction()
        }

        binding.btnHistory.setOnClickListener {
            // Nối màn hình lịch sử chấm công ở bước 6
            Toast.makeText(this, "Tính năng Lịch sử chấm công (Bước 6)", Toast.LENGTH_SHORT).show()
        }

        binding.btnMySalary.setOnClickListener {
            // Nối màn hình bảng lương ở bước 7
            Toast.makeText(this, "Tính năng Bảng lương của tôi (Bước 7)", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTodayStatus()
    }

    private fun loadTodayStatus() {
        val accountId = sessionManager.getAccountId()
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val displayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        binding.tvToday.text = "Hôm nay: $displayDate"

        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            val employee = dao.getEmployeeByAccountId(accountId) ?: return@launch
            currentEmployee = employee

            binding.tvWelcome.text = "Xin chào, ${employee.fullname}"

            // 1. Tự động chuyển các WorkLog ngày cũ còn CHECKED_IN sang INCOMPLETE
            val oldLogs = dao.getOldCheckedInWorkLogs(employee.id, todayDate)
            for (oldLog in oldLogs) {
                dao.updateWorkLog(oldLog.copy(status = "INCOMPLETE"))
            }

            // 2. Lấy WorkLog hôm nay
            val todayLog = dao.getWorkLogByEmployeeAndDate(employee.id, todayDate)
            currentTodayLog = todayLog

            when {
                todayLog == null -> {
                    // Chưa check-in
                    binding.tvStatus.text = "Chưa check-in"
                    binding.btnCheck.text = "Check-in"
                    binding.btnCheck.isEnabled = true
                }
                todayLog.status == "CHECKED_IN" -> {
                    // Đã check-in, chưa check-out
                    binding.tvStatus.text = "Đã check-in lúc ${todayLog.checkInTime}"
                    binding.btnCheck.text = "Check-out"
                    binding.btnCheck.isEnabled = true
                }
                todayLog.status == "COMPLETED" -> {
                    // Đã hoàn thành chấm công hôm nay
                    val hoursStr = if (todayLog.totalHours % 1.0 == 0.0) {
                        todayLog.totalHours.toLong().toString()
                    } else {
                        String.format(Locale.US, "%.2f", todayLog.totalHours)
                    }
                    binding.tvStatus.text = "Hoàn thành ${todayLog.checkInTime} – ${todayLog.checkOutTime} (${hoursStr}h)"
                    binding.btnCheck.text = "Đã chấm công hôm nay"
                    binding.btnCheck.isEnabled = false
                }
                else -> {
                    // INCOMPLETE
                    binding.tvStatus.text = "Chấm công chưa hoàn thành"
                    binding.btnCheck.text = "Đã chấm công hôm nay"
                    binding.btnCheck.isEnabled = false
                }
            }
        }
    }

    private fun handleCheckAction() {
        val employee = currentEmployee ?: return
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val log = currentTodayLog

        if (log == null) {
            // CHECK-IN
            lifecycleScope.launch {
                val dao = AppDB.getInstance(applicationContext).appDao()
                val existing = dao.getWorkLogByEmployeeAndDate(employee.id, todayDate)
                if (existing != null) {
                    Toast.makeText(this@EmployeeHomeActivity, "Hôm nay bạn đã check-in rồi!", Toast.LENGTH_SHORT).show()
                    loadTodayStatus()
                    return@launch
                }

                val newLog = WorkLog(
                    employeeId = employee.id,
                    workDate = todayDate,
                    checkInTime = currentTime,
                    checkOutTime = null,
                    totalHours = 0.0,
                    status = "CHECKED_IN"
                )
                dao.insertWorkLog(newLog)
                Toast.makeText(this@EmployeeHomeActivity, "Check-in thành công lúc $currentTime", Toast.LENGTH_SHORT).show()
                loadTodayStatus()
            }
        } else if (log.status == "CHECKED_IN") {
            // CHECK-OUT
            val checkInMin = timeToMinutes(log.checkInTime)
            val checkOutMin = timeToMinutes(currentTime)

            if (checkOutMin <= checkInMin) {
                Toast.makeText(this@EmployeeHomeActivity, "Giờ check-out phải lớn hơn giờ check-in!", Toast.LENGTH_SHORT).show()
                return
            }

            val minutesDiff = checkOutMin - checkInMin
            val totalHours = kotlin.math.round((minutesDiff / 60.0) * 100.0) / 100.0

            lifecycleScope.launch {
                val dao = AppDB.getInstance(applicationContext).appDao()
                val updatedLog = log.copy(
                    checkOutTime = currentTime,
                    totalHours = totalHours,
                    status = "COMPLETED"
                )
                dao.updateWorkLog(updatedLog)
                Toast.makeText(this@EmployeeHomeActivity, "Check-out thành công lúc $currentTime ($totalHours giờ)", Toast.LENGTH_SHORT).show()
                loadTodayStatus()
            }
        }
    }

    private fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size != 2) return 0
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        return hours * 60 + minutes
    }

    private fun logout() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
