package com.example.asteroidradarapp.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.asteroidradarapp.Constants
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

    private val _asteroidFilter = MutableLiveData(NasaApiFilter.SHOW_SAVE)

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
    get() = _status

     //Use LiveDAta
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroid: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //Create the navigation
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPictureOfDay()
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
    }
//    init {
//        getAsteroidProperties()
//        getPictureOfDay()
//    }

    val asteroidList = asteroidsRepository.asteroids

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
                val result = withContext(Dispatchers.IO) {
                    NasaApi.retrofitService.getPictureOfDay(Constants.API_KEY)
                }
                _pictureOfDay.value = result
                _status.value = "   image URL : ${_pictureOfDay.value!!.url}"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }


    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    // Click func to filter menu items based on enum parameters
    fun updateFilter (filters: NasaApiFilter) {
        _asteroidFilter.postValue(filters)
    }
}