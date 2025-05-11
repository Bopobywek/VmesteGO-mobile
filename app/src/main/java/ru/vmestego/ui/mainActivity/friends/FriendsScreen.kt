package ru.vmestego.ui.mainActivity.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ru.vmestego.R
import ru.vmestego.routing.SecondaryLevelRoute
import ru.vmestego.ui.mainActivity.friends.friendsTab.FriendsTabScreen
import ru.vmestego.ui.mainActivity.friends.invitationsTab.InvitationsTabScreen
import ru.vmestego.ui.mainActivity.friends.feedTab.FeedTabScreen

@Serializable
object FeedTab

@Serializable
object FriendsTab

@Serializable
object InvitationsTab

val friendsPageRoutes = listOf(
    SecondaryLevelRoute("FeedTab", FeedTab, R.string.feed_tab_name),
    SecondaryLevelRoute("FriendsTab", FriendsTab, R.string.friends_tab_name),
    SecondaryLevelRoute("InvitationsTab", InvitationsTab, R.string.invitations_tab_name)
)

// https://stackoverflow.com/a/70303650
@Composable
fun FriendsScreen(goToUserScreen: (Long) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        content = { paddingValues ->
            NavHost(
                navController,
                startDestination = FeedTab,
                Modifier.Companion.padding(paddingValues)
            ) {
                composable<FeedTab> { FeedTabScreen() }
                composable<FriendsTab> { FriendsTabScreen(goToUserScreen) }
                composable<InvitationsTab> { InvitationsTabScreen() }
            }
        }
    )
}

@Composable
fun TopBar(navController: NavHostController) {
    val tabIndex = remember { mutableIntStateOf(0) }
    val shape = RoundedCornerShape(20.dp)
    Column {
        TabRow(
            selectedTabIndex = tabIndex.intValue,
            Modifier.Companion.padding(bottom = 15.dp)
        ) {
            friendsPageRoutes.forEachIndexed { index, route ->
                Tab(
                    selected = tabIndex.intValue == index,
                    onClick = {
                        tabIndex.intValue = index
                        navController.navigate(route.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(route.localizedNameResourceId),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
            }
        }
    }
}