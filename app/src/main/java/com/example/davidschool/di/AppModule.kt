package com.example.davidschool.di

import android.content.Context
import com.example.davidschool.database.AppDatabase
import com.example.davidschool.database.DatabaseDao
import com.example.davidschool.database.DatabaseHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)


    @Singleton
    @Provides
    fun provideDataDao(db: AppDatabase) = db.getDao()

    @Singleton
    @Provides
    fun provideDatabaseHelper(databaseDao: DatabaseDao) = DatabaseHelper(databaseDao)

}