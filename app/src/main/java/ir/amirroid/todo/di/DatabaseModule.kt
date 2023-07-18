package ir.amirroid.todo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.amirroid.todo.models.db.AppDatabase
import ir.amirroid.todo.utils.Constants
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.APP_DATABASE
    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase) = appDatabase.taskDao()

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase) = appDatabase.categoryDao()
}