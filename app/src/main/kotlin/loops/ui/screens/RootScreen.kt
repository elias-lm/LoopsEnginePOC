package loops.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

/**
    *
    *  RootScreen is the main entry point for the application.
    *  It is responsible for setting up the navigation between the different screens.
    *
    *  @param rootScreenViewModel The [RootScreenViewModel] used to manage the application state.
    *
    *  @return A [Scaffold] with the [NavHost] set up.
    */
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun RootScreen(rootScreenViewModel: RootScreenViewModel) {

    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    //https://developer.android.com/guide/navigation/navigation-type-safety
    LaunchedEffect("navigation") {
        rootScreenViewModel.navigator.destination.collect {
            navController.navigate(it.route)
        }
    }

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            topBar = topAppBar(),
        ) {
            NavHost(
                navController = navController,
                startDestination = ScreenDestinations.Loops.Graph.route
            ) {
                navigation(
                    startDestination = ScreenDestinations.Loops.Main.route,
                    route = ScreenDestinations.Loops.Graph.route
                ) {
                    composable(route = ScreenDestinations.Loops.Main.route) {
                        val conversationsViewModel: LoopsViewModel =
                            getViewModel { parametersOf(rootScreenViewModel.savedStateHandle) }
                        LoopsView(conversationsViewModel)
                    }
                }
                navigation(
                    startDestination = ScreenDestinations.Login.Main.route,
                    route = ScreenDestinations.Login.Graph.route
                ) {
                    composable(route = ScreenDestinations.Login.Main.route) {

                    }
                }
                navigation(
                    startDestination = ScreenDestinations.Logged.Main.route,
                    route = ScreenDestinations.Logged.Graph.route
                ) {
                    composable(route = ScreenDestinations.Logged.Main.route) {
                        val mainViewModel: MainViewModel =
                            getViewModel { parametersOf(rootScreenViewModel.savedStateHandle) }
                        MainView(mainViewModel)
                    }
                }
            }
        }
    }

    }

@Composable
fun topAppBar(): @Composable () -> Unit = {
    TopAppBar(
        title = {
            Text("Title Here")
        },
        actions = {
            Button(onClick = {}) {
                Text(text = "Click")
            }
        }
    )
}
