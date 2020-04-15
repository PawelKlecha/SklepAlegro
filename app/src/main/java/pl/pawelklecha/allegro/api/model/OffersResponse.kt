package pl.pawelklecha.allegro.api.model

import com.google.gson.annotations.SerializedName

data class OffersResponse(
    @SerializedName("offers")
    val offers: List<Offer>
)