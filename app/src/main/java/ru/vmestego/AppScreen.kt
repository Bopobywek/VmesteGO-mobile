package ru.vmestego

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Search", Search, Icons.Filled.Search),
    TopLevelRoute("Tickets", Tickets, Icons.Filled.Info),
    TopLevelRoute("Friends", Friends, Icons.Filled.Person)
)

@Serializable
object Tickets

@Serializable
object Search

@Serializable
object Friends

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppScreen() {
    // https://www.youtube.com/watch?v=c8XP_Ee7iqY
    // https://github.com/aman246149/Jetpack-compose-tips/blob/8f185d5080b1c55b01080ca96d66d743974c342d/app/src/main/java/com/example/udemycompose/routing/routing.kt
    // https://developer.android.com/develop/ui/compose/navigation
    val navController = rememberNavController()
    val selectedIndex = rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            ) {
                topLevelRoutes.forEachIndexed { index, topLevelRoute ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                topLevelRoute.icon,
                                contentDescription = topLevelRoute.name
                            )
                        },
                        label = { Text(topLevelRoute.name) },
                        selected = selectedIndex.asIntState().intValue == index,
                        onClick = {
                            selectedIndex.intValue = index
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Tickets, Modifier.padding(innerPadding)) {
            composable<Search> { SearchScreen() }
            composable<Tickets> { TicketsScreen() }
            composable<Friends> { FriendsScreen() }
        }
    }
}