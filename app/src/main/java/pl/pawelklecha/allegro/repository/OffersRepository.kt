package pl.pawelklecha.allegro.repository

import androidx.lifecycle.LiveData
import pl.pawelklecha.allegro.api.AllegroService
import pl.pawelklecha.allegro.api.ApiSuccessResponse
import pl.pawelklecha.allegro.api.model.Offer
import pl.pawelklecha.allegro.api.model.OffersResponse
import pl.pawelklecha.allegro.api.model.Resource
import pl.pawelklecha.allegro.app.AppExecutors
import pl.pawelklecha.allegro.db.AllegroDb
import pl.pawelklecha.allegro.db.OffersDao
import pl.pawelklecha.allegro.util.RateLimiter
import java.util.concurrent.TimeUnit

class OffersRepository(
    private val appExecutors: AppExecutors,
    private val apiService: AllegroService,
    private val db: AllegroDb,
    private val offersDao: OffersDao
) {
    private val repoListRateLimit = RateLimiter<String>(5, TimeUnit.MINUTES)

    // SwipeRefresh forces data fetch and skips RateLimiter
    fun getAllOffers(forceRefresh: Boolean): LiveData<Resource<List<Offer>>> {
        return object : NetworkBoundResource<List<Offer>, OffersResponse>(appExecutors) {

            override fun saveCallResult(item: OffersResponse) {
                db.runInTransaction {
                    offersDao.clearOffers()
                    offersDao.insertOffers(item.offers.filter { offer ->
                        offer.price.amount in 50.0..1000.0
                    })
                }
            }

            override fun shouldFetch(data: List<Offer>?): Boolean {
                return data == null
                        || data.isEmpty()
                        || forceRefresh
                        || repoListRateLimit.shouldFetch(SHOULD_FETCH_KEY)
            }


            override fun loadFromDb(): LiveData<List<Offer>> {
                return offersDao.getAllOffers()
            }

            override fun createCall() = apiService.getAllOffers()

            override fun processResponse(response: ApiSuccessResponse<OffersResponse>)
                    : OffersResponse {
                return response.body
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(SHOULD_FETCH_KEY)
            }
        }.asLiveData()
    }

    companion object {
        private const val SHOULD_FETCH_KEY = "SHOULD_FETCH"
    }
}
