package me.quenchjian.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.quenchjian.webservice.FakeApi
import me.quenchjian.webservice.RestApi

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
abstract class FakeNetworkModule {

  @Binds
  abstract fun bindApi(api: FakeApi): RestApi
}
