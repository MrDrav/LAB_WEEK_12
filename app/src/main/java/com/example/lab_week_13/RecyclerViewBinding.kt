package com.example.lab_week_13

import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.BindingAdapter
import com.example.lab_week_13.model.Movie
import java.util.Calendar

@BindingAdapter("list")
fun bindMovies(view: RecyclerView, movies: List<Movie>?) {
    val adapter = view.adapter as MovieAdapter
    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    val filteredMovies = (movies ?: emptyList())
        .filter { it.releaseDate?.startsWith(currentYear) == true }
        .sortedByDescending { it.popularity }

    adapter.addMovies(filteredMovies)
}
