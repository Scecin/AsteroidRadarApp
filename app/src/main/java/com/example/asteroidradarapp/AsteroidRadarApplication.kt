package com.example.asteroidradarapp

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.asteroidradarapp.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * We’re going to schedule The workManager in our application class. This is the Android
 * application which is the main object that the operating system uses to interact with your app.
 */
class AsteroidRadarApplication: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, repeatingRequest)
    }
}