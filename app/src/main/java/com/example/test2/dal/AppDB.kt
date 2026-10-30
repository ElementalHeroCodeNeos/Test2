package com.example.test2.dal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.test2.entities.Account
import com.example.test2.entities.Employee
import com.example.test2.entities.Salary
import com.example.test2.entities.WorkLog
import java.util.concurrent.Executors

@Database(
    entities = [Account::class, Employee::class, WorkLog::class, Salary::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getInstance(context: Context): AppDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "employee_management.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                seedDatabase(getInstance(context).appDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun seedDatabase(dao: AppDao) {
            runBlockingSeed(dao)
        }

        private fun runBlockingSeed(dao: AppDao) {
            kotlinx.coroutines.runBlocking {
                // Seed Admin Account
                val adminAccount = Account(
                    username = "admin",
                    password = "admin",
                    role = "ADMIN"
                )
                dao.insertAccount(adminAccount)

                // Seed NV01 Account & Employee
                val nv01Account = Account(
                    username = "NV01",
                    password = "123",
                    role = "EMPLOYEE"
                )
                val nv01AccountId = dao.insertAccount(nv01Account)

                val employee = Employee(
                    code = "NV01",
                    fullname = "Nguyễn Văn A",
                    email = "nva@gmail.com",
                    phone = "0901234567",
                    hourlyRate = 50000.0,
                    accountId = nv01AccountId
                )
                val employeeId = dao.insertEmployee(employee)

                // Seed 10 COMPLETED WorkLogs for month 9/2026
                val seedLogs = listOf(
                    WorkLog(employeeId = employeeId, workDate = "2026-09-01", checkInTime = "08:00", checkOutTime = "17:00", totalHours = 9.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-02", checkInTime = "08:00", checkOutTime = "17:00", totalHours = 9.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-03", checkInTime = "08:00", checkOutTime = "18:00", totalHours = 10.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-04", checkInTime = "08:00", checkOutTime = "16:00", totalHours = 8.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-05", checkInTime = "08:00", checkOutTime = "17:00", totalHours = 9.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-07", checkInTime = "08:00", checkOutTime = "19:00", totalHours = 11.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-08", checkInTime = "08:00", checkOutTime = "17:00", totalHours = 9.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-09", checkInTime = "08:00", checkOutTime = "17:00", totalHours = 9.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-10", checkInTime = "08:00", checkOutTime = "18:00", totalHours = 10.0, status = "COMPLETED"),
                    WorkLog(employeeId = employeeId, workDate = "2026-09-11", checkInTime = "08:00", checkOutTime = "16:00", totalHours = 8.0, status = "COMPLETED")
                )

                for (log in seedLogs) {
                    dao.insertWorkLog(log)
                }
            }
        }
    }
}
