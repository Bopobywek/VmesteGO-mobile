package ru.vmestego

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.math.min
import kotlin.random.Random

data class SecondaryLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val localizedNameResourceId: Int
)

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
fun FeedTabScreen() {
    FeedEventsList(
        listOf(
            EventUi("test", "test", LocalDate.now()),
            EventUi("test", "test", LocalDate.now()),
            EventUi("test", "test", LocalDate.now()),
            EventUi("test", "test", LocalDate.now()),
            EventUi("test", "test", LocalDate.now())
        )
    )
}

@Composable
fun FeedEventsList(events: List<EventUi>) {
    val grouped = events.groupBy { it.date.withDayOfMonth(1) }
    val ordered = grouped.toSortedMap()

    // https://stackoverflow.com/a/74227507
    val initialIndex = getCurrentMonthHeaderIndex(ordered)
    Log.i("Tickets", "current index $initialIndex")
    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        state = listState
    ) {
        ordered.forEach { (date, dateEvents) ->
            item {
                DateHeader(date)
            }

            val sortedEvents = dateEvents.sortedBy { t -> t.date }
            items(sortedEvents) { event ->
                EventCard(event)
            }
        }
    }
}

// https://stackoverflow.com/a/78475807
@Composable
fun EventCard(event: EventUi) {
    val profileImageSize = 24
    val boxContentPadding = 15

    // https://stackoverflow.com/a/69688759
    Box(
        Modifier.padding(15.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .heightIn(min = 100.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(boxContentPadding.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        append("welcome to ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.W900,
                                color = Color(0xFF4552B8)
                            )
                        ) {
                            append("Jetpack Compose Playground")
                        }
                    }
                )
                Text(
                    buildAnnotatedString {
                        append("Now you are in the ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                            append("Card")
                        }
                        append(" section")
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .offset(x = boxContentPadding.dp, y = (profileImageSize / 2).dp)
                .align(Alignment.BottomStart)
        ) {
            val maxImages = 5
            val sz = (0..10).random()
            for (i in 0..min(sz, maxImages - 1)) {
                val randomColor = generateWarmSoftColor()
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(randomColor),
                    modifier = Modifier
                        .offset(x = (-i * profileImageSize / 2).dp)
                        .size(profileImageSize.dp)
                        .clip(CircleShape)
                )
            }

            if (sz > maxImages) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .offset(x = (-maxImages * profileImageSize / 2).dp)
                        .size(profileImageSize.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

fun generateWarmSoftColor(): Color {
    val red = Random.nextFloat() * 0.3f + 0.7f  // 70% - 100% Red
    val green = Random.nextFloat() * 0.3f + 0.5f // 50% - 80% Green
    val blue = Random.nextFloat() * 0.3f + 0.4f  // 40% - 70% Blue
    return Color(red, green, blue, 1f) // Full opacity
}

data class EventUi(
    val eventName: String,
    val locationName: String,
    val date: LocalDate = LocalDate.now()
)

@Composable
fun FriendsTabScreen() {
    Text("Friends")
}

@Composable
fun InvitationsTabScreen() {
    Text("Invitations")
}

// https://stackoverflow.com/a/70303650
@Preview(showBackground = true)
@Composable
fun FriendsScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        content = { paddingValues ->
            NavHost(
                navController,
                startDestination = FeedTab,
                Modifier.padding(paddingValues)
            ) {
                composable<FeedTab> { FeedTabScreen() }
                composable<FriendsTab> { FriendsTabScreen() }
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
