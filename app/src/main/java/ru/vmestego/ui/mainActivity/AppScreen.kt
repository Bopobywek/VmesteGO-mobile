package ru.vmestego.ui.mainActivity

import ru.vmestego.ui.mainActivity.eventForm.EventFormScreen
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import ru.vmestego.data.EventDataDto
import ru.vmestego.ui.mainActivity.event.EventScreenWrapper
import ru.vmestego.ui.mainActivity.event.EventViewModel
import ru.vmestego.ui.mainActivity.event.EventViewModelFactory
import ru.vmestego.routing.IconizedRoute
import ru.vmestego.ui.mainActivity.friends.FriendsScreen
import ru.vmestego.ui.mainActivity.profile.OtherUserProfileScreenWrapper
import ru.vmestego.ui.mainActivity.profile.OtherUserProfileViewModel
import ru.vmestego.ui.mainActivity.profile.OtherUserProfileViewModelFactory
import ru.vmestego.ui.mainActivity.profile.ProfileScreen
import ru.vmestego.ui.mainActivity.search.SearchScreen
import ru.vmestego.ui.mainActivity.tickets.TicketsScreen
import ru.vmestego.ui.ticketActivity.EventParametersViewModel
import java.time.LocalDateTime

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
data class CustomEvent(
    val id: Long? = null
)

@Serializable
data class Event(
    val id: Long
)

@Serializable
data class User(
    val id: Long
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppScreen() {
    // https://www.youtube.com/watch?v=c8XP_Ee7iqY
    // https://github.com/aman246149/Jetpack-compose-tips/blob/8f185d5080b1c55b01080ca96d66d743974c342d/app/src/main/java/com/example/udemycompose/routing/routing.kt
    // https://developer.android.com/develop/ui/compose/navigation
    val navController = rememberNavController()
    val selectedIndex = rememberSaveable { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

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
        val context = LocalContext.current
        NavHost(navController, startDestination = Tickets, Modifier.padding(innerPadding)) {
            composable<Search> {
                SearchScreen(
                    goToEvent = {
                        navController.navigate(Event(it.id)) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    createEvent = {
                        navController.navigate(CustomEvent()) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
            composable<Tickets> { TicketsScreen() }
            composable<Friends> {
                FriendsScreen {
                    navController.navigate(User(it))
                }
            }
            composable<Profile> {
                ProfileScreen {
                    navController.navigate(Event(it.id)) {
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            }
            composable<CustomEvent> { backStackEntry ->
                if (backStackEntry.lifecycleIsResumed()) {
                    val route = backStackEntry.toRoute<CustomEvent>()
                    EventFormScreen(existingEventId = route.id) {
                        val viewModel =
                            EventParametersViewModel(context.applicationContext as Application)
                        scope.launch(Dispatchers.IO) {
                            viewModel.addEvent(
                                EventDataDto(
                                    it.title,
                                    it.location,
                                    LocalDateTime.of(it.date, it.time),
                                    it.externalId
                                )
                            )

                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
            composable<Event> { backStackEntry ->
                if (backStackEntry.lifecycleIsResumed()) {
                    val route = backStackEntry.toRoute<Event>()
                    val viewModel: EventViewModel =
                        viewModel(
                            factory = EventViewModelFactory(
                                LocalContext.current.applicationContext as Application,
                                route.id
                            )
                        )
                    EventScreenWrapper(viewModel,
                        editEvent = {
                            navController.navigate(CustomEvent(it)) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) { navController.popBackStack() }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
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
                OtherUserProfileScreenWrapper(viewModel) {
                    navController.navigate(Event(it.id)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

// https://www.reddit.com/r/androiddev/comments/13xyei9/comment/jmndzbw/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED