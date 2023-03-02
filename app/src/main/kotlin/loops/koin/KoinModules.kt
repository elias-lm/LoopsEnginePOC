package loops.koin

import android.media.AudioManager
import loops.LoopsEngine
import loops.android.SensorsProxy
import loops.ui.Navigator
import loops.ui.screens.LoopsViewModel
import loops.ui.screens.MainViewModel
import loops.ui.screens.RootScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { params -> RootScreenViewModel(params.get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { LoopsViewModel(get(), get(), get(), get(), get()) }

}

val services = module {

    singleOf(::Navigator)
    singleOf(::SensorsProxy)
    single { androidContext().getSystemService(AudioManager::class.java) }
    single { androidContext().assets }

    single { LoopsEngine(androidContext().assets) }
}

val reposModule = module {
}
