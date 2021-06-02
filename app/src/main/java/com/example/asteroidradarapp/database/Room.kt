package com.example.asteroidradarapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.*

//Create a Database Access object fou our offline cache and tell the UI hat the database changed somehow
@Dao
interface AsteroidDao {
    @Query("select * from databaseAsteroid order by closeApproachDate desc")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseAsteroid where closeApproachDate = :todayDate order by closeApproachDate desc")
    fun getTodayAsteroids(todayDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

// Implement a database
@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

// Define getAsteroidDatabase
private lateinit var INSTANCE: AsteroidsDatabase

fun getAsteroidDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java, "asteroids"
            ).build()
        }
    }
    return INSTANCE
}