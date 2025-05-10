package com.creative.pexels.di

import com.creative.pexels.data.favorite.FavoriteDataSource
import com.creative.pexels.data.favorite.FavoriteDataSourcePref
import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.data.source.PhotoDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providePhotoDataSource(photoDataSourceImpl: PhotoDataSourceImpl): PhotoDataSource = photoDataSourceImpl

    @Singleton
    @Provides
    fun provideFavoriteDataSource(favoriteDataSourcePref: FavoriteDataSourcePref): FavoriteDataSource = favoriteDataSourcePref
}