package com.example.test2.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Salary",
    indices = [
        Index(value = ["employeeId", "month", "year"], unique = true)
    ]
)
data class Salary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: Long,
    val month: Int,
    val year: Int,
    val totalHours: Double,
    val overtimeHours: Double,
    val totalSalary: Double,
    val status: String // "PENDING" | "PAID"
)
