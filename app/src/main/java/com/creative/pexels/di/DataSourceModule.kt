package com.creative.pexels.di

import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.data.source.PhotoDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun providePhotoDataSource(photoDataSourceImpl: PhotoDataSourceImpl): PhotoDataSource = photoDataSourceImpl
}