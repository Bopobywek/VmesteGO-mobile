package ru.vmestego.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

fun provideOkHttpClient(context: Context): OkHttpClient {
    val cacheSize = 10L * 1024 * 1024
    val cache = Cache(File(context.cacheDir, "coil_cache"), cacheSize)

    return OkHttpClient.Builder()
        .cache(cache)
        .addNetworkInterceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .build()
        }
        .build()
}

@Composable
fun rememberCachedImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember {
        ImageLoader.Builder(context)
            .okHttpClient { provideOkHttpClient(context) }
            .respectCacheHeaders(true)
            .build()
    }
}