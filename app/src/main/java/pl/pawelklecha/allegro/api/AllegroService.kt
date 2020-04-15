package pl.pawelklecha.allegro.api

import androidx.lifecycle.LiveData
import pl.pawelklecha.allegro.api.model.OffersResponse
import retrofit2.http.GET

/**
 * API Service
 */
interface AllegroService {
    @GET("offers")
    fun getAllOffers(): LiveData<ApiResponse<OffersResponse>>

}