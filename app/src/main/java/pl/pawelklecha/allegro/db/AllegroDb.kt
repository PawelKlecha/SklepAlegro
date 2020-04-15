package pl.pawelklecha.allegro.db


import androidx.room.Database
import androidx.room.RoomDatabase
import pl.pawelklecha.allegro.api.model.Offer

/**
 * Room database for application.
 */
@Database(
    entities = [
        Offer::class],
    version = 3,
    exportSchema = false
)
abstract class AllegroDb : RoomDatabase() {

    abstract fun offersDao(): OffersDao

}