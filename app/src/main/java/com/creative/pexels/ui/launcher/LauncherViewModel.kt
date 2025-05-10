package com.creative.pexels.ui.launcher

import android.util.Log
import androidx.lifecycle.ViewModel
import com.creative.pexels.data.source.PhotoDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val dataSource: PhotoDataSource
) : ViewModel(), ILauncherViewModel {
    init {
        Log.d("LauncherViewModel", "init $dataSource")
    }
}