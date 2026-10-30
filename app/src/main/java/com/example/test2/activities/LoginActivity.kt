package com.example.test2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test2.activities.admin.AdminActivity
import com.example.test2.activities.employee.EmployeeHomeActivity
import com.example.test2.dal.AppDB
import com.example.test2.databinding.ActivityLoginBinding
import com.example.test2.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val dao = AppDB.getInstance(applicationContext).appDao()
            val account = dao.getAccountByUsername(username)

            if (account == null || account.password != password) {
                Toast.makeText(this@LoginActivity, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                return@launch
            }

            sessionManager.saveSession(account)

            if (account.role == "ADMIN") {
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
            } else {
                startActivity(Intent(this@LoginActivity, EmployeeHomeActivity::class.java))
            }
            finish()
        }
    }
}
