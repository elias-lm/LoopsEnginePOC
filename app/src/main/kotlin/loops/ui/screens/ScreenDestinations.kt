package loops.ui.screens

sealed class ScreenDestinations(val route: String, val data: Any? = null) {

    sealed class Login(subRoute: String) : ScreenDestinations("$route/$subRoute") {
        object Graph : Login("")
        object Main : Login("main")
        companion object {
            val route = "login"
        }
    }

    sealed class Logged(subRoute: String) : ScreenDestinations("$route/$subRoute") {
        object Graph : Logged("")
        object Main : Logged("main")
        companion object {
            val route = "logged"
        }
    }

    sealed class Loops(subRoute: String) : ScreenDestinations("$route/$subRoute") {
        object Graph : Loops("")
        object Main : Loops("main")

        companion object {
            val route = "loops"
        }
    }


}