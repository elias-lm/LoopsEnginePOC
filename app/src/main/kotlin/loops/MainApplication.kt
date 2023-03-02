package loops

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import loops.koin.reposModule
import loops.koin.services
import loops.koin.viewModelsModule
//import mad.dev.common.CommonClass
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SHARED_PREFERENCES = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        startKoin {
            androidContext(this@MainApplication)
            modules(
                viewModelsModule,
                reposModule,
                services
            )

//            CommonClass()
        }
    }

    companion object {
        lateinit var SHARED_PREFERENCES: SharedPreferences
    }
}

private const val SHARED_PREFERENCES_NAME = "loops"

