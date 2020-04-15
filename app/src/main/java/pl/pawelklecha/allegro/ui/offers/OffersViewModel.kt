package pl.pawelklecha.allegro.ui.offers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import pl.pawelklecha.allegro.api.model.Offer
import pl.pawelklecha.allegro.api.model.Resource
import pl.pawelklecha.allegro.repository.OffersRepository

/**
 * Paging should be used there, but given api doesn't support it
 * @link https://developer.android.com/topic/libraries/architecture/paging
 */
class OffersViewModel(offersRepository: OffersRepository) : ViewModel() {
    private val _refresh = MutableLiveData(false)
    val offers: LiveData<Resource<List<Offer>>> = _refresh.switchMap {
        offersRepository.getAllOffers(it)
    }

    //Possibility to bypass working 5 minutes on local cache values and force refresh data from the network.
    fun refresh(forceRefresh: Boolean) {
        _refresh.value = forceRefresh
    }
}