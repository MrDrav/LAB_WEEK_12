package com.example.lab_week_13

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Get the repository from the application
        val movieRepository = (applicationContext as MovieApplication).movieRepository

        // Fetch movies from network in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieRepository.fetchMoviesFromNetwork()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Result.success()
    }
}
