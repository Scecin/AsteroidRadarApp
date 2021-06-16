package com.example.asteroidradarapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.asteroidradarapp.Constants
import com.example.asteroidradarapp.Constants.API_KEY
import com.example.asteroidradarapp.database.AsteroidsDatabase
import com.example.asteroidradarapp.database.asDatabaseModel
import com.example.asteroidradarapp.database.asDomainModel
import com.example.asteroidradarapp.domain.Asteroid
import com.example.asteroidradarapp.domain.PictureOfDay
import com.example.asteroidradarapp.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class AsteroidRepository(private val database: AsteroidsDatabase) {

    private var startDate = getStartDateFormatted()
    private var endDate = getEndDateFormatted()


    val asteroid: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it?.asDomainModel()
        }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = NasaApi.retrofitService.getAsteroids(startDate, endDate, API_KEY)
                val parsedAsteroids = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(parsedAsteroids.asDatabaseModel())
            } catch (e: Exception) {
                Log.w("ERROR", e.message.toString())
            }
        }
    }

    //Method to refresh PictureOfTheDay Offline Cache
    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = NasaApi.retrofitService.getPictureOfDay(API_KEY)
            if (pictureOfDay.mediaType == "image") {
                database.pictureOfDayDao.clear()
                database.pictureOfDayDao.insertAll(pictureOfDay.asDatabaseModel())
            }
        }
    }
}