package com.example.test2.activities.employee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test2.activities.LoginActivity
import com.example.test2.dal.AppDB
import com.example.test2.databinding.ActivityEmployeeHomeBinding
import com.example.test2.utils.SessionManager
import kotlinx.coroutines.launch

class EmployeeHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        loadEmployeeInfo()

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadEmployeeInfo() {
        val accountId = sessionManager.getAccountId()
        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            val employee = dao.getEmployeeByAccountId(accountId)
            val name = employee?.fullname ?: sessionManager.getUsername()
            binding.tvWelcome.text = "Xin chào, $name"
        }
    }

    private fun logout() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
