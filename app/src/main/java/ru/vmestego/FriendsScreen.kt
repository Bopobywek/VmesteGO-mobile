package ru.vmestego

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

@Composable
fun InvitationsTabScreen() {
    Column {
        Button(
            onClick = {},
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .height(50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    Modifier.fillMaxWidth(0.9f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("Исходящие")
                }
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        // fff5ee
                        .background(Color(1f, 0.9607843137254902f, 0.9333f, 1f))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "2",
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Входящие", Modifier.padding(start = 20.dp), color = Color.DarkGray)
        Spacer(Modifier.height(2.dp))
        HorizontalDivider(Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        Spacer(Modifier.height(10.dp))
        InvitationsList()
    }
}

@Composable
fun InvitationsList() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        for (i in 1..10) {
            item {
                val formatter = DateTimeFormatter.ofPattern("EE, dd MMM. yyyy", Locale("ru")) // Russian locale
                val formattedDate = LocalDate.now().format(formatter)
                val context = LocalContext.current

                Box(
                    Modifier.padding(horizontal = 20.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .heightIn(min = 100.dp)
                            .fillMaxWidth()) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)) {
                            Row {
                                Column {
                                    Text(text = "test name", Modifier.fillMaxWidth(0.5f))
                                }
                                Column {
                                    Text(text = formattedDate)
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(Icons.Filled.Place, "Add")
                                Text(text = "test")
                            }
                        }
                    }
                }
            }
        }
    }
}

// https://stackoverflow.com/a/70303650
@Composable
fun FriendsScreen(goToUserScreen: (Int) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        content = { paddingValues ->
            NavHost(
                navController,
                startDestination = InvitationsTab,
                Modifier.padding(paddingValues)
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
            modifier = Modifier
                .clip(shape = shape)
                .padding(20.dp)
                .background(Color.LightGray, shape)
                .padding(10.dp),
            // https://stackoverflow.com/a/73448228
            divider = {},
            indicator = {},
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
                    },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Black
                )
            }
        }
    }
}
