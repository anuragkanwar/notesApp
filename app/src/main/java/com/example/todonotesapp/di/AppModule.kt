package com.example.todonotesapp.di

import android.content.Context
import androidx.room.Room
import com.example.todonotesapp.data.NoteDao
import com.example.todonotesapp.data.NoteDatabase
import com.example.todonotesapp.network.NoteApi
import com.example.todonotesapp.utils.Constants
import com.example.todonotesapp.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

    @Singleton
    @Provides
    fun provideNotesDao(noteDatabase: NoteDatabase): NoteDao = noteDatabase.getNoteDao()


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "notes_db"
        )
            .fallbackToDestructiveMigration()
            .build()


    @Singleton
    @Provides
    fun provideNotesApi() : NoteApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoteApi::class.java)
    }


}