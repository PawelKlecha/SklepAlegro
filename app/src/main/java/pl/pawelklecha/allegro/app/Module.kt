package pl.pawelklecha.allegro.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.pawelklecha.allegro.api.AllegroService
import pl.pawelklecha.allegro.db.AllegroDb
import pl.pawelklecha.allegro.db.OffersDao
import pl.pawelklecha.allegro.repository.OffersRepository
import pl.pawelklecha.allegro.repository.PreferenceRepository
import pl.pawelklecha.allegro.ui.offers.OffersViewModel
import pl.pawelklecha.allegro.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

/**
 * All of these dependencies will be injected into other components.
 */
private const val ALLEGRO_PREFERENCES = "allegro_preferences"

val viewModelModule = module {
    single { OffersViewModel(get()) }
}

val databaseModule = module {
    fun provideDb(application: Application): AllegroDb {
        return Room
            .databaseBuilder(application, AllegroDb::class.java, "allegro.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideOffersDao(db: AllegroDb): OffersDao {
        return db.offersDao()
    }

    single { provideDb(androidApplication()) }
    single { provideOffersDao(get()) }
}

val retrofitModule = module {
    fun provideRetrofit(): AllegroService {
        return Retrofit.Builder()
            .baseUrl("https://private-987cdf-allegromobileinterntest.apiary-mock.com/allegro/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(AllegroService::class.java)
    }

    single { provideRetrofit() }
}

val repositoryModule = module {
    fun provideOffersRepository(
        apiService: AllegroService,
        appExecutors: AppExecutors,
        db: AllegroDb,
        dao: OffersDao
    ): OffersRepository {
        return OffersRepository(appExecutors, apiService, db, dao)
    }

    fun providePreferencesRepository(sharedPreferences: SharedPreferences): PreferenceRepository {
        return PreferenceRepository(sharedPreferences)
    }

    single { providePreferencesRepository(get()) }
    single { provideOffersRepository(get(), get(), get(), get()) }
}

val sharedPreferencesModule = module {
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(ALLEGRO_PREFERENCES, Context.MODE_PRIVATE)
    }

    single { provideSharedPreferences(androidApplication()) }
}

val appExecutors = module {
    single {
        AppExecutors(
            Executors.newSingleThreadExecutor(),
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)
        )
    }
}