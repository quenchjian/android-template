package me.quenchjian.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.data.TaskRepository

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataModule::class])
abstract class FakeDataModule {

  @Binds
  abstract fun bindTaskRepository(repository: FakeTaskRepository): TaskRepository
}
