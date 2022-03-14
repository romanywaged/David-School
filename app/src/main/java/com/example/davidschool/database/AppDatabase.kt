package com.example.davidschool.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.davidschool.database.model.*
import com.example.davidschool.utils.DATABASE_NAME


@Database(entities = [Child::class, Khedma::class, Attendance::class, AttendanceChildrenRef::class], exportSchema = false, version = 7)

abstract class AppDatabase : RoomDatabase(){
    abstract fun getDao() : DatabaseDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    context
                    //mDataBase
                ).also { instance = it }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}