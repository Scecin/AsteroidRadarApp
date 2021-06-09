package com.example.asteroidradarapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.asteroidradarapp.Constants
import com.example.asteroidradarapp.Constants.API_KEY
import com.example.asteroidradarapp.database.AsteroidsDatabase
import com.example.asteroidradarapp.database.asDomainModel
import com.example.asteroidradarapp.domain.Asteroid
import com.example.asteroidradarapp.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class AsteroidRepository(private val database: AsteroidsDatabase) {

    private var startDate = getNextSevenDaysFormattedDates()[0]
    private var endDate = getNextSevenDaysFormattedDates()[Constants.DEFAULT_END_DATE_DAYS]


    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it?.asDomainModel()
        }

//    val pictureOfDay: LiveData<PictureOfDay> =
//        Transformations.map(database.asteroidDao.getPictureOfDay()) {
//            it?.let {
//                it.asDomainModel()
//            }
//        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = NasaApi.retrofitService.getAsteroids(startDate, endDate, API_KEY)
                val parsedAsteroids = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(NetworkAsteroidContainer(parsedAsteroids).asDatabaseModel())
            } catch (e: Exception) {
                Log.w("ERROR", e.message.toString())
            }

        }
    }

}