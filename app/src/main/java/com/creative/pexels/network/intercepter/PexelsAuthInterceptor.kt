package com.creative.pexels.network.intercepter

import com.creative.pexels.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by dan on 9/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

class PexelsAuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", BuildConfig.PEXELS_API_KEY)
            .build()
        return chain.proceed(request)
    }
}