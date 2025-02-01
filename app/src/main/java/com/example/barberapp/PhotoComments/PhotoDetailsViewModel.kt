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

    private val _ratings = MutableLiveData<List<Rating>>()
    val ratings: LiveData<List<Rating>> get() = _ratings

    /**
     * Carrega os comentários pela URL da foto.
     */
    fun loadRatingsByPhotoUrl(photoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ratingsList = ratingDao.getRatingsByPhotoUrlSync(photoUrl)
                withContext(Dispatchers.Main) {
                    _ratings.value = ratingsList
                }
            } catch (e: Exception) {
                Log.e("PhotoDetailsViewModel", "Erro ao carregar comentários: ${e.message}")
            }
        }
    }

    /**
     * Insere um novo comentário e recarrega a lista automaticamente.
     */
    fun insertRating(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ratingDao.insert(rating)
                Log.d("PhotoDetailsViewModel", "Comentário inserido com sucesso!")

                // **Após inserir, recarregar os comentários**
                val updatedRatings = ratingDao.getRatingsByPhotoUrlSync(rating.photoUrl)
                withContext(Dispatchers.Main) {
                    _ratings.value = updatedRatings
                }
            } catch (e: Exception) {
                Log.e("PhotoDetailsViewModel", "Erro ao inserir comentário: ${e.message}")
            }
        }
    }
}
