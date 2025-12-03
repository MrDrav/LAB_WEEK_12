package com.example.lab_week_13

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MovieApplication : Application() {
    lateinit var movieRepository: MovieRepository
    lateinit var movieDatabase: MovieDatabase

    override fun onCreate() {
        super.onCreate()
        
        // create the Room database instance
        movieDatabase = MovieDatabase.getDatabase(this)
        
        // create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        // create a MovieService instance
        // and bind the MovieService interface to the Retrofit instance
        val movieService = retrofit.create(MovieService::class.java)

        // create a MovieRepository instance with database
        movieRepository = MovieRepository(movieService, movieDatabase)

        // Setup WorkManager for periodic background sync
        setupWorkManager()
    }

    private fun setupWorkManager() {
        // Create constraints - only run when network is available
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create periodic work request (runs every 1 hour)
        val movieWorkRequest = PeriodicWorkRequestBuilder<MovieWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        // Enqueue the work request
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "movie_sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            movieWorkRequest
        )
    }
}
