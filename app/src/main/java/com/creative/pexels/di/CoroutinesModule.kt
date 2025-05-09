package com.creative.pexels.di

import com.creative.pexels.dispatchers.AppDispatchers
import com.creative.pexels.dispatchers.AppDispatchersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Singleton
    @Provides
    fun provideAppDispatchers(appDispatchersImpl: AppDispatchersImpl): AppDispatchers = appDispatchersImpl
}