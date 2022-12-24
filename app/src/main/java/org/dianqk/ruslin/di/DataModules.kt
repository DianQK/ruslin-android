package org.dianqk.ruslin.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.dianqk.ruslin.data.NotesRepository
import org.dianqk.ruslin.data.RuslinNotesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModel {

    @Singleton
    @Provides
    fun provideNotesRepository(@ApplicationContext appContext: Context): NotesRepository {
        val databaseDir = appContext.getDatabasePath("database.sql").parent!!
        Log.d("RepositoryModel", "provideNotesRepository $databaseDir")
        return RuslinNotesRepository(databaseDir)
    }
}