package ru.vmestego.ui.mainActivity.friends.friendsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable
import ru.vmestego.R
import ru.vmestego.routing.SecondaryLevelRoute
import ru.vmestego.ui.models.UserUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsTabScreen(goToUserScreen: (Long) -> Unit, viewModel: FriendsTabViewModel = viewModel()) {
    val showBottomSheet = remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = {
                showBottomSheet.value = true
            },
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
                    Text("Заявки")
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
                        text = viewModel.incomingFriendsRequests.collectAsState().value.size.toString(),
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }

        val colors1 = SearchBarDefaults.colors()
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = viewModel.searchText,//text showed on SearchBar
                    onQueryChange = viewModel::onQueryChanged, //update the value of searchText
                    onSearch = viewModel::onSearch, //the callback to be invoked when the input service triggers the ImeAction.Search action
                    expanded = false, // whether the user is searching or not
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = { Text("Поиск...") },
                    leadingIcon = null,
                    trailingIcon = null,
                    interactionSource = null,
                )
            },
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(15.dp),
            colors = colors1,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets
        ) {}

        Spacer(Modifier.height(10.dp))

        val users by viewModel.users.collectAsState()
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (users.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text("Тут пока пусто", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
                    Text("Скорее добавьте друзей", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        else {
            FriendsList(goToUserScreen, users)
        }
    }

    FriendsRequestsModalSheet(showBottomSheet, viewModel, goToUserScreen)
}

@Composable
fun FriendsList(goToUserScreen: (Long) -> Unit, users: List<UserUi>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(users) { user ->
            Box(
                Modifier
                    .padding(horizontal = 20.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(15.dp))
                    .clickable {
                        goToUserScreen(user.id)
                    }
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = user.imageUrl,
                        error = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.W400,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}


@Serializable
object IncomingFriendsRequests

@Serializable
object OutcomingFriendsRequests

val friendsRequestsRoutes = listOf(
    SecondaryLevelRoute(
        "IncomingFriendsRequests",
        IncomingFriendsRequests,
        R.string.incoming_friends_requests_tab_name
    ),
    SecondaryLevelRoute(
        "OutcomingFriendsRequests",
        OutcomingFriendsRequests,
        R.string.outcoming_friends_requests_tab_name
    ),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsRequestsModalSheet(
    showBottomSheet: MutableState<Boolean>,
    viewModel: FriendsTabViewModel,
    goToUserScreen: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            val navController = rememberNavController()

            Scaffold(
                topBar = {
                    FriendsRequestsTopBar(navController)
                },
                content = { paddingValues ->
                    NavHost(
                        navController,
                        startDestination = IncomingFriendsRequests,
                        Modifier.padding(paddingValues)
                    ) {
                        composable<IncomingFriendsRequests> {
                            FriendsIncomingRequestsScreen(
                                viewModel,
                                goToUserScreen
                            )
                        }
                        composable<OutcomingFriendsRequests> {
                            FriendsOutcomingRequestsScreen(
                                viewModel,
                                goToUserScreen
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun FriendsIncomingRequestsScreen(
    viewModel: FriendsTabViewModel,
    goToUserScreen: (Long) -> Unit
) {
    val incomingRequests by viewModel.incomingFriendsRequests.collectAsState()
    LazyColumn(Modifier.padding(top = 15.dp)) {
        items(incomingRequests) { request ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .clickable { goToUserScreen(request.from.id) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = request.from.imageUrl,
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = request.from.name,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(Modifier.padding(horizontal = 15.dp)) {
                Button(
                    onClick = { viewModel.acceptFriendRequest(request) },
                    Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Принять")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { viewModel.declineFriendRequest(request) },
                    Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Отклонить")
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 15.dp))

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun FriendsOutcomingRequestsScreen(
    viewModel: FriendsTabViewModel,
    goToUserScreen: (Long) -> Unit
) {
    val outgoingRequests by viewModel.outgoingFriendsRequests.collectAsState()
    LazyColumn(
        modifier = Modifier
            .padding(top = 15.dp)
    ) {
        items(outgoingRequests) { request ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .clickable {
                        goToUserScreen(request.to.id)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = request.to.imageUrl,
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = request.to.name,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Button(onClick = {
                    viewModel.cancelFriendRequest(request)
                }, shape = RoundedCornerShape(10.dp)) {
                    Text("Отменить")
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 15.dp))

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun FriendsRequestsTopBar(navController: NavHostController) {
    val tabIndex = remember { mutableIntStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = tabIndex.intValue,
        ) {
            friendsRequestsRoutes.forEachIndexed { index, route ->
                Tab(
                    selected = tabIndex.intValue == index,
                    onClick = {
                        tabIndex.intValue = index
                        navController.navigate(route.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
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
