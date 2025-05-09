package com.creative.pexels.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

class AppDispatchersTestImpl : AppDispatchers {
    override val io: CoroutineDispatcher
        get() = StandardTestDispatcher(name = "io")
    override val main: CoroutineDispatcher
        get() = StandardTestDispatcher(name = "main")
    override val computation: CoroutineDispatcher
        get() = StandardTestDispatcher(name = "computation")
}