package com.example.asteroidradarapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.asteroidradarapp.database.getAsteroidDatabase
import com.example.asteroidradarapp.repository.AsteroidRepository
import retrofit2.HttpException

// Create a worker which extend coroutine worker.

class RefreshDataWorker (appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getAsteroidDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids()
//            repository.refreshPictureOfDay()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}