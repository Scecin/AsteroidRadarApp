package com.example.asteroidradarapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.asteroidradarapp.database.getAsteroidDatabase
import com.example.asteroidradarapp.repository.AsteroidRepository
import retrofit2.HttpException

/**
 * We're going to use Work|Manager to perform tasks when our app is in the background
 * or even not running.
 */
class RefreshDataWorker (appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getAsteroidDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}