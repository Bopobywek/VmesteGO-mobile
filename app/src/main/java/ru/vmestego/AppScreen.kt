package ru.vmestego

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import java.time.LocalDate

val iconizedRoutes = listOf(
    IconizedRoute("Search", Search, Icons.Filled.Search),
    IconizedRoute("Tickets", Tickets, Icons.Filled.Info),
    IconizedRoute("Friends", Friends, Icons.Filled.Favorite),
    IconizedRoute("Profile", Profile, Icons.Filled.Person)
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
    val id: Int
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
                    navController.navigate(Event(12))
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
                Log.i("EventRedirect", route.id.toString())
                EventScreenWrapper(EventUi(
                    eventName = "Икар",
                    locationName = "КЗ Измайлово",
                    date = LocalDate.now(),
                    description = "В мире «Икара» около 50 лет назад произошла глобальная война. Применялось биологическое оружие, которое уничтожило взрослое население, и в живых остались только дети и подростки. При этом они частично потеряли память. События происходят в период, когда на руинах цивилизации поднялся Полис — город, возведенный на основе секретной военной базы. Он закрыт защитным Куполом и концентрирует в себе все ресурсы и технологии, собранные из разрушенного мира. Полис процветает. Все, кто вне Полиса — с трудом выживают..."
                ), { navController.popBackStack() })
            }
            composable<User> {
                backStackEntry ->
                val route = backStackEntry.toRoute<User>()
                Log.i("UserRedirect", route.id.toString())
                val viewModel: OtherUserProfileViewModel =
                    viewModel(factory = OtherUserProfileViewModelFactory(LocalContext.current.applicationContext as Application, route.id))
                OtherUserProfileScreenWrapper(viewModel)
            }
        }
    }
}