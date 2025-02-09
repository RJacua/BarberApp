package com.example.barberapp.PhotoComments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val ratingDao = AppDatabase(application).ratingDao()
    private val clientDao = AppDatabase(application).clientDao()

    private val _ratings = MutableLiveData<List<Pair<Rating, String?>>>()
    val ratings: LiveData<List<Pair<Rating, String?>>> get() = _ratings


    /**
     * Load ratings by photo url
     * Loads comments (ratings) for a specific photo based on its URL.
     *
     * @param photoUrl
     */
    fun loadRatingsByPhotoUrl(photoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ratingsList = ratingDao.getRatingsByPhotoUrlSync(photoUrl)

                // Fetch client names for each rating
                val ratingsWithClientNames = ratingsList.map { rating ->
                    val client = clientDao.getClientById(rating.clientId)
                    rating to client?.name // Returns client name or `null`
                }

                // Update LiveData on the main thread
                withContext(Dispatchers.Main) {
                    _ratings.value = ratingsWithClientNames
                }
            } catch (e: Exception) {
                Log.e("PhotoDetailsViewModel", "Error loading comments: ${e.message}")
            }
        }
    }
}
