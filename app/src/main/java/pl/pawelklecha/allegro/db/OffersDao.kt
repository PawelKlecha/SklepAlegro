package pl.pawelklecha.allegro.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.pawelklecha.allegro.api.model.Offer

/**
 * Interface for database access for Offer related operations.
 */
@Dao
interface OffersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOffers(offers: List<Offer>)

    @Query("SELECT * FROM Offer ORDER BY price_amount")
    fun getAllOffers(): LiveData<List<Offer>>

    @Query("DELETE FROM Offer")
    fun clearOffers()
}
