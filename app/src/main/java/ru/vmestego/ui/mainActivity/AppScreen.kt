package ru.vmestego.ui.mainActivity

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.vmestego.event.EventScreenWrapper
import ru.vmestego.event.EventUi
import ru.vmestego.routing.IconizedRoute
import java.time.Instant
import java.time.ZoneId

val iconizedRoutes = listOf(
    IconizedRoute("Поиск", Search, Icons.Filled.Search),
    IconizedRoute("Билеты", Tickets, Icons.Filled.LocalActivity),
    IconizedRoute("Друзья", Friends, Icons.Filled.Group),
    IconizedRoute("Профиль", Profile, Icons.Filled.Person)
)

@Serializable
object Tickets

@Serializable
object Search

@Serializable
object Friends

@Serializable
object Profile

@Serializable
data class Event(
    val id: Long,
    val eventName: String,
    val locationName: String,
    val date: Long,
    val description: String
)

@Serializable
data class User(
    val id: Int
)

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
                    .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
                    .fillMaxWidth()
            ) {
                iconizedRoutes.forEachIndexed { index, topLevelRoute ->
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
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Tickets, Modifier.padding(innerPadding)) {
            composable<Search> {
                SearchScreen() {
                    val zoneId = ZoneId.systemDefault();
                    val epoch = it.date.atStartOfDay(zoneId).toEpochSecond()
                    navController.navigate(Event(it.id, it.eventName, it.locationName, epoch, it.description))
                }
            }
            composable<Tickets> { TicketsScreen() }
            composable<Friends> {
                FriendsScreen({
                    navController.navigate(User(it))
                })
            }
            composable<Profile> { ProfileScreen() }
            composable<Event> { backStackEntry ->
                val route = backStackEntry.toRoute<Event>()
                val dt = Instant.ofEpochSecond(route.date).atZone(ZoneId.systemDefault()).toLocalDate()
                EventScreenWrapper(
                    EventUi(
                    route.id,
                    route.eventName,
                    route.locationName,
                    dt,
                    route.description
                ), { navController.popBackStack() })
            }
            composable<User> { backStackEntry ->
                val route = backStackEntry.toRoute<User>()
                Log.i("UserRedirect", route.id.toString())
                val viewModel: OtherUserProfileViewModel =
                    viewModel(
                        factory = OtherUserProfileViewModelFactory(
                            LocalContext.current.applicationContext as Application,
                            route.id
                        )
                    )
                OtherUserProfileScreenWrapper(viewModel)
            }
        }
    }
}