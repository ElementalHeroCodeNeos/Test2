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

@Database(
    entities = [Account::class, Employee::class, WorkLog::class, Salary::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    abstract fun appDao(): DAO

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed Admin Account
                            db.execSQL("INSERT INTO Account (id, username, password, role) VALUES (1, 'admin', 'admin', 'ADMIN');")

                            // Seed NV01 Account & Employee
                            db.execSQL("INSERT INTO Account (id, username, password, role) VALUES (2, 'NV01', '123', 'EMPLOYEE');")
                            db.execSQL("INSERT INTO Employee (id, code, fullname, email, phone, hourlyRate, accountId) VALUES (1, 'NV01', 'Nguyễn Văn A', 'nva@gmail.com', '0901234567', 50000.0, 2);")

                            // Seed 10 COMPLETED WorkLogs for month 9/2026
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-01', '08:00', '17:00', 9.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-02', '08:00', '17:00', 9.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-03', '08:00', '18:00', 10.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-04', '08:00', '16:00', 8.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-05', '08:00', '17:00', 9.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-07', '08:00', '19:00', 11.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-08', '08:00', '17:00', 9.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-09', '08:00', '17:00', 9.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-10', '08:00', '18:00', 10.0, 'COMPLETED');")
                            db.execSQL("INSERT INTO WorkLog (employeeId, workDate, checkInTime, checkOutTime, totalHours, status) VALUES (1, '2026-09-11', '08:00', '16:00', 8.0, 'COMPLETED');")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
