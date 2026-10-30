package com.example.test2.dal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.test2.entities.Account
import com.example.test2.entities.Employee
import com.example.test2.entities.Salary
import com.example.test2.entities.SalaryView
import com.example.test2.entities.WorkLog

@Dao
interface DAO {

    // --- ACCOUNT ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAccount(account: Account): Long

    @Query("SELECT * FROM Account WHERE username = :username LIMIT 1")
    suspend fun getAccountByUsername(username: String): Account?

    @Query("SELECT * FROM Account WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): Account?

    @Query("DELETE FROM Account WHERE id = :id")
    suspend fun deleteAccountById(id: Long)

    // --- EMPLOYEE ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("SELECT * FROM Employee WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: Long): Employee?

    @Query("SELECT * FROM Employee WHERE accountId = :accountId LIMIT 1")
    suspend fun getEmployeeByAccountId(accountId: Long): Employee?

    @Query("SELECT * FROM Employee WHERE code = :code LIMIT 1")
    suspend fun getEmployeeByCode(code: String): Employee?

    @Query("SELECT * FROM Employee WHERE email = :email LIMIT 1")
    suspend fun getEmployeeByEmail(email: String): Employee?

    @Query("SELECT * FROM Employee ORDER BY id DESC")
    suspend fun getAllEmployees(): List<Employee>

    @Query("SELECT * FROM Employee WHERE code LIKE '%' || :query || '%' OR fullname LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchEmployees(query: String): List<Employee>

    @Transaction
    suspend fun deleteEmployeeFull(employee: Employee) {
        deleteWorkLogsByEmployeeId(employee.id)
        deleteSalariesByEmployeeId(employee.id)
        deleteEmployee(employee)
        deleteAccountById(employee.accountId)
    }

    // --- WORKLOG ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkLog(workLog: WorkLog): Long

    @Update
    suspend fun updateWorkLog(workLog: WorkLog)

    @Query("DELETE FROM WorkLog WHERE employeeId = :employeeId")
    suspend fun deleteWorkLogsByEmployeeId(employeeId: Long)

    @Query("SELECT * FROM WorkLog WHERE employeeId = :employeeId AND workDate = :workDate LIMIT 1")
    suspend fun getWorkLogByEmployeeAndDate(employeeId: Long, workDate: String): WorkLog?

    @Query("SELECT * FROM WorkLog WHERE employeeId = :employeeId AND workDate LIKE :yearMonthPattern ORDER BY workDate DESC")
    suspend fun getWorkLogsByEmployeeAndMonthPattern(employeeId: Long, yearMonthPattern: String): List<WorkLog>

    @Query("SELECT * FROM WorkLog WHERE employeeId = :employeeId AND workDate LIKE :yearMonthPattern AND status = 'COMPLETED'")
    suspend fun getCompletedWorkLogsByEmployeeAndMonthPattern(employeeId: Long, yearMonthPattern: String): List<WorkLog>

    @Query("SELECT * FROM WorkLog WHERE employeeId = :employeeId AND status = 'CHECKED_IN' AND workDate < :todayDate")
    suspend fun getOldCheckedInWorkLogs(employeeId: Long, todayDate: String): List<WorkLog>

    // --- SALARY ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSalary(salary: Salary): Long

    @Update
    suspend fun updateSalary(salary: Salary)

    @Query("DELETE FROM Salary WHERE employeeId = :employeeId")
    suspend fun deleteSalariesByEmployeeId(employeeId: Long)

    @Query("SELECT * FROM Salary WHERE employeeId = :employeeId AND month = :month AND year = :year LIMIT 1")
    suspend fun getSalaryByEmployeeAndMonthYear(employeeId: Long, month: Int, year: Int): Salary?

    @Query("""
        SELECT Salary.*, Employee.code AS code, Employee.fullname AS fullname 
        FROM Salary 
        INNER JOIN Employee ON Salary.employeeId = Employee.id 
        WHERE Salary.month = :month AND Salary.year = :year 
        ORDER BY Employee.code ASC
    """)
    suspend fun getSalaryViewsByMonthYear(month: Int, year: Int): List<SalaryView>

    @Query("""
        SELECT Salary.*, Employee.code AS code, Employee.fullname AS fullname 
        FROM Salary 
        INNER JOIN Employee ON Salary.employeeId = Employee.id 
        WHERE Salary.employeeId = :employeeId 
        ORDER BY Salary.year DESC, Salary.month DESC
    """)
    suspend fun getSalaryViewsByEmployee(employeeId: Long): List<SalaryView>
}
