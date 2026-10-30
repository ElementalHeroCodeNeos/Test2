package com.example.test2.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "WorkLog",
    indices = [
        Index(value = ["employeeId", "workDate"], unique = true)
    ]
)
data class WorkLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: Long,
    val workDate: String, // "yyyy-MM-dd"
    val checkInTime: String, // "HH:mm"
    val checkOutTime: String?, // nullable, "HH:mm"
    val totalHours: Double = 0.0,
    val status: String // "CHECKED_IN" | "COMPLETED" | "INCOMPLETE"
)
