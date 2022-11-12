package com.example.todonotesapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.utils.DateConverter
import com.example.todonotesapp.utils.ListConverters

@Database(entities = [LocalNote::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class,ListConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao() : NoteDao

}