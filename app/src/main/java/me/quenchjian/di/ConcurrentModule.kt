package me.quenchjian.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.quenchjian.concurrent.DefaultScheduler
import me.quenchjian.concurrent.Schedulers

@Module
@InstallIn(SingletonComponent::class)
abstract class ConcurrentModule {

  @Binds
  abstract fun bindScheduler(scheduler: DefaultScheduler): Schedulers
}
