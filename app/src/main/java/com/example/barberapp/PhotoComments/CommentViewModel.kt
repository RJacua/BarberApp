package com.example.barberapp.PhotoComments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentViewModel(application: Application) : AndroidViewModel(application) {

    private val ratingDao = AppDatabase(application).ratingDao()

    /**
     * Inserts a new rating into the database.
     *
     * @param rating
     */
    fun insertRating(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            ratingDao.insert(rating)
        }
    }
}