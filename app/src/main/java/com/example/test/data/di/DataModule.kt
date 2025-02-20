package com.example.test.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.test.data.network.GithubService
import com.example.test.data.repository.LocalRepositoryImpl
import com.example.test.data.repository.RemoteRepositoryImpl
import com.example.test.data.repository.UserDataRepositoryImpl
import com.example.test.data.room.AppDatabase
import com.example.test.data.room.BookmarkDao
import com.example.test.domain.LocalRepository
import com.example.test.domain.RemoteRepository
import com.example.test.domain.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideSearchRepository(service: GithubService): RemoteRepository =
        RemoteRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideLocalRepository(bookmarkDao: BookmarkDao): LocalRepository =
        LocalRepositoryImpl(bookmarkDao)


    @Provides
    @Singleton
    fun provideUserDataRepository(dataStore: DataStore<Preferences>): UserDataRepository =
        UserDataRepositoryImpl(dataStore)

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room
        .databaseBuilder(context, AppDatabase::class.java, "local.db")
        .build()

    @Singleton
    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase): BookmarkDao = appDatabase.bookmarkDao()

    private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences"
    )

    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> {
        return applicationContext.userDataStore
    }

}