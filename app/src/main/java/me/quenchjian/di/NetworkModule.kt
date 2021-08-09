package me.quenchjian.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.quenchjian.webservice.RestApi
import me.quenchjian.webservice.WebApi

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

  @Binds
  abstract fun bindApi(api: WebApi): RestApi
}
