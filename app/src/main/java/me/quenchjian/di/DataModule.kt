package me.quenchjian.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.quenchjian.BuildConfig
import me.quenchjian.data.TaskRepository
import me.quenchjian.data.db.TaskDAO
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

  companion object {
    @Provides
    @Named("DatabaseName")
    fun provideDatabaseName() = if (BuildConfig.DEBUG) "task_debug.db" else "task.db"
  }

  @Binds
  abstract fun bindTaskRepository(repository: TaskDAO): TaskRepository
}
