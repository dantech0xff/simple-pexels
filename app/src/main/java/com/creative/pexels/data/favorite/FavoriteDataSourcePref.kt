package com.creative.pexels.data.favorite

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.creative.pexels.dispatchers.AppDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dan on 11/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

internal val Context.localDataStore: DataStore<Preferences> by preferencesDataStore(name = "FavoriteDataSourcePref")

@Singleton
class FavoriteDataSourcePref @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDispatchers: AppDispatchers
) : FavoriteDataSource {
    companion object {
        val PreferenceStringSetPhotoIds = stringSetPreferencesKey("fav_photos")
    }

    override suspend fun isPhotoFavorite(photoId: Long) = withContext(appDispatchers.io) {
        context.localDataStore.data.map {
            it[PreferenceStringSetPhotoIds]?.contains(photoId.toString()) == true
        }.first()
    }

    override suspend fun updatePhotoFavorite(photoId: Long, isFavorite: Boolean) = withContext(appDispatchers.io) {
        context.localDataStore.edit {
            val prev = it[PreferenceStringSetPhotoIds]?.toMutableSet() ?: mutableSetOf()
            if (isFavorite) {
                prev.add(photoId.toString())
            } else {
                prev.remove(photoId.toString())
            }
            it[PreferenceStringSetPhotoIds] = prev
        }
        Unit
    }
}
