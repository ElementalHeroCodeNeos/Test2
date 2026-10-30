package com.example.test2.entities

import androidx.room.Embedded

data class SalaryView(
    @Embedded val salary: Salary,
    val code: String,
    val fullname: String
)
