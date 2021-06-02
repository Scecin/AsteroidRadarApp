//package com.example.asteroidradarapp.network
//
//import com.example.asteroidradarapp.database.DatabaseAsteroid
//import com.squareup.moshi.JsonClass
//
//@JsonClass(generateAdapter = true)
//data class NetworkAsteroidContainer(val asteroids: List<NetworkAsteroid>)
//
//@JsonClass(generateAdapter = true)
//data class NetworkAsteroid(
//    val id: Long,
//    val codename: String,
//    val closeApproachDate: String,
//    val absoluteMagnitude: Double,
//    val estimatedDiameter: Double,
//    val relativeVelocity: Double,
//    val distanceFromEarth: Double,
//    val isPotentiallyHazardous: Boolean
//)
//
//fun NetworkAsteroidContainer.asDatabaseModel(): Array<DatabaseAsteroid> {
//    return asteroids.map {
//        DatabaseAsteroid(
//            id = it.id,
//            codename = it.codename,
//            closeApproachDate = it.closeApproachDate,
//            absoluteMagnitude = it.absoluteMagnitude,
//            estimatedDiameter = it.estimatedDiameter,
//            relativeVelocity = it.relativeVelocity,
//            distanceFromEarth = it.distanceFromEarth,
//            isPotentiallyHazardous = it.isPotentiallyHazardous
//        )
//    }.toTypedArray()
//}