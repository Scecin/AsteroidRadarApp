package com.example.asteroidradarapp.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.asteroidradarapp.Constants
import com.example.asteroidradarapp.database.getAsteroidDatabase
import com.example.asteroidradarapp.domain.Asteroid
import com.example.asteroidradarapp.domain.PictureOfDay
import com.example.asteroidradarapp.network.NasaApi
import com.example.asteroidradarapp.network.NasaApiFilter
import com.example.asteroidradarapp.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : ViewModel() {

    // Do reference a database
    private val database = getAsteroidDatabase(application)

    // Do reference a repository
    private val asteroidsRepository = AsteroidRepository(database)

    val asteroidList = asteroidsRepository.asteroid
    val picOfDay = asteroidsRepository.pictureOfDay


    private val _asteroidFilter = MutableLiveData(NasaApiFilter.SHOW_SAVE)

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    //Create the navigation
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    init {
        try {
            viewModelScope.launch {
                asteroidsRepository.refreshAsteroids()
                asteroidsRepository.refreshPictureOfDay()
            }
        } catch (e: Exception) {
            Log.w("ERROR", e.message.toString())
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    // Click func to filter menu items based on enum parameters
    fun updateFilter(filters: NasaApiFilter) {
        _asteroidFilter.postValue(filters)
    }
}