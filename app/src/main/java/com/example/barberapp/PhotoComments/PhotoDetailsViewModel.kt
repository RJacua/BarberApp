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


class PhotoDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val ratingDao = AppDatabase(application).ratingDao()

    private val _photoUrl = MutableLiveData<String>()  // LiveData para a URL da foto

    private val _ratings = MutableLiveData<List<Rating>>()
    val ratings: LiveData<List<Rating>> get() = _ratings


    /**
     * Função que carrega as avaliações pela URL da foto.
     *
     * @param photoUrl URL da foto que queremos filtrar as avaliações.
     */
    fun loadRatingsByPhotoUrl(photoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ratingsList = ratingDao.getRatingsByPhotoUrlSync(photoUrl)
                _ratings.postValue(ratingsList)  // Sempre atualize o LiveData
            } catch (e: Exception) {
                Log.e("PhotoDetailsViewModel", "Erro ao carregar comentários: ${e.message}")
            }
        }
    }
    
    fun insertRating(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ratingDao.insert(rating)
                Log.d("PhotoDetailsViewModel", "Comentário inserido com sucesso!")
            } catch (e: Exception) {
                Log.e("PhotoDetailsViewModel", "Erro ao inserir comentário: ${e.message}")
            }
        }
    }

}