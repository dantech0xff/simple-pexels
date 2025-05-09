package com.creative.pexels.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface AppDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val computation: CoroutineDispatcher
}