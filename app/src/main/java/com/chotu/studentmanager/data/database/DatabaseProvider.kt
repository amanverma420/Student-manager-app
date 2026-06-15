package com.chotu.studentmanager.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: StudentDatabase? = null

    fun getDatabase(
        context: Context
    ): StudentDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                StudentDatabase::class.java,
                "student_database"
            ).build().also { INSTANCE = it }
        }
    }
}
