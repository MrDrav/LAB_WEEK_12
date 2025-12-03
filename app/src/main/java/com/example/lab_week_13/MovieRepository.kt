package com.example.lab_week_13

import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDatabase
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {
    private val apiKey = "0e200cb62bc69f5171d5e92216878294"
    private val movieDao = movieDatabase.movieDao()

    // fetch movies with offline-first logic
    // this function returns a Flow of Movie objects
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // First, check if there's data in the local database
            val localMovies = movieDao.getMovies()
            
            if (localMovies.isEmpty()) {
                // If no local data, fetch from API
                val remoteMovies = movieService.getPopularMovies(apiKey).results
                // Save to local database
                movieDao.insertMovies(remoteMovies)
                // Emit API data
                emit(remoteMovies)
            } else {
                // If there's local data, emit it
                emit(localMovies)
            }
        }.flowOn(Dispatchers.IO) // run on background thread
    }
}
