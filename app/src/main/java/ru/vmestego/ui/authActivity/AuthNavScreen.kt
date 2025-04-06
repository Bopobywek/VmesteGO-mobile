package ru.vmestego.ui.authActivity

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object Authorization

@Serializable
object Registration

@Composable
fun AuthNavScreen() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(navController, startDestination = Authorization, Modifier.padding(innerPadding)) {
            composable<Authorization> { AuthScreen { navController.navigate(Registration) } }
            composable<Registration> { RegistrationScreen { navController.popBackStack() } }
        }
    }
}