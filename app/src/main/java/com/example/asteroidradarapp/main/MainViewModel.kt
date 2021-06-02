package com.example.asteroidradarapp.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.asteroidradarapp.Constants.API_KEY
import com.example.asteroidradarapp.database.getAsteroidDatabase
import com.example.asteroidradarapp.domain.Asteroid
import com.example.asteroidradarapp.domain.PictureOfDay
import com.example.asteroidradarapp.network.NasaApi
import com.example.asteroidradarapp.network.NasaApiFilter
import com.example.asteroidradarapp.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Do reference a database
    private val database = getAsteroidDatabase(application)

    // Do reference a repository
    private val asteroidsRepository = AsteroidRepository(database)
    var asteroidsList = asteroidsRepository.asteroidList

    // Use LiveDAta
//    private val _asteroids = MutableLiveData<List<Asteroid>>()
//    val asteroid: LiveData<List<Asteroid>>
//        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //Create the navigation
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    init {
        getAsteroidProperties()
        getPictureOfDay()
    }

    private fun getAsteroidProperties() {
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {
                Log.d("ggg", "error: $e")
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = NasaApi.retrofitService.getPictureOfDay(API_KEY)
            } catch (e: Exception) {
                Log.d("ggg", "error: $e")
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}