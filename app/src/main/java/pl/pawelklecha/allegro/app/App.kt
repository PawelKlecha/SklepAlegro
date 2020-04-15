package pl.pawelklecha.allegro.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Start Koin and inject created modules. The main reason why should we use Koin(Dependency Injection)
 * is that it makes testing applications easier and allows classes to define their dependencies
 * without constructing them. At runtime, another class is responsible for providing these dependencies.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(
                listOf(
                    viewModelModule,
                    databaseModule,
                    retrofitModule,
                    repositoryModule,
                    sharedPreferencesModule,
                    appExecutors
                )
            )
        }
    }
}