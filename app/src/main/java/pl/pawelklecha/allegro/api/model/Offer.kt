package pl.pawelklecha.allegro.api.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Offer(
    @PrimaryKey
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("price")
    @Embedded(prefix = "price_")
    val price: Price,
    @SerializedName("description")
    val description: String
) : Parcelable

@Parcelize
data class Price(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String
) : Parcelable