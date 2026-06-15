package com.chotu.studentmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chotu.studentmanager.data.dao.StudentDao
import com.chotu.studentmanager.data.entity.StudentEntity

@Database(
    entities = [StudentEntity::class],
    version = 1,
    exportSchema = false
)

abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}