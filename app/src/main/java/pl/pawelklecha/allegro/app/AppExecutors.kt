package pl.pawelklecha.allegro.app

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
/**
 * Custom executors to work on, there is also the possibility to use Kotlin coroutines instead of that.
 */
open class AppExecutors(
    val diskIO: Executor,
    val networkIO: Executor,
    val mainThread: Executor = MainThreadExecutor()
) {

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}