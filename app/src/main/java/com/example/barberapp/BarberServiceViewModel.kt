import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.barberapp.data.AppDatabase
import com.example.barberapp.data.BarberServiceDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarberServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val barberServiceDao = AppDatabase(application).barberserviceDao()

    // LiveData para armazenar os serviços do barbeiro
    private val _services = MutableLiveData<List<BarberServiceDetail>>()
    val services: LiveData<List<BarberServiceDetail>> get() = _services

    // Função para carregar os serviços pelo ID do barbeiro
    fun loadBarberServices(barberId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val serviceList = barberServiceDao.getDetailedServicesByBarber(barberId)
            _services.postValue(serviceList) // Atualiza o LiveData com os dados

        }
    }
}
