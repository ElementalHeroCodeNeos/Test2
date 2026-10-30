package com.example.test2.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Employee",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val hourlyRate: Double,
    val accountId: Long
)
