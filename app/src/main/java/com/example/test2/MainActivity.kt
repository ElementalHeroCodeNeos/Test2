package com.example.test2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.activities.LoginActivity
import com.example.test2.activities.admin.AdminActivity
import com.example.test2.activities.employee.EmployeeHomeActivity
import com.example.test2.utils.SessionManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            when (sessionManager.getRole()) {
                "ADMIN" -> startActivity(Intent(this, AdminActivity::class.java))
                "EMPLOYEE" -> startActivity(Intent(this, EmployeeHomeActivity::class.java))
                else -> startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
