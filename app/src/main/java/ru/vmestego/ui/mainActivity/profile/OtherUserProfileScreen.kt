package ru.vmestego.ui.mainActivity.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.vmestego.R
import ru.vmestego.ui.models.FriendRequestStatusUi
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.utils.rememberCachedImageLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreenWrapper(
    viewModel: OtherUserProfileViewModel,
    goToEvent: (EventUi) -> Unit) {
    val state = rememberPullToRefreshState()

    if (viewModel.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = viewModel.isLoading,
            onRefresh = viewModel::updateStatuses,
            state = state
        ) {
            OtherUserProfileScreen(viewModel, goToEvent)
        }
    }
}

@Composable
fun OtherUserProfileScreen(
    viewModel: OtherUserProfileViewModel,
    goToEvent: (EventUi) -> Unit) {
    val userInfo by viewModel.userInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val imageLoader = rememberCachedImageLoader()
            AsyncImage(
                model = userInfo?.imageUrl,
                imageLoader = imageLoader,
                error = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userInfo?.name ?: "", // TODO: handle loading
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val incomingRequestStatus by viewModel.incomingRequestStatus.collectAsState()
        if (incomingRequestStatus != FriendRequestStatusUi.None) {
            when (incomingRequestStatus) {
                FriendRequestStatusUi.Pending -> {
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Button(
                            onClick = viewModel::acceptIncomingRequest,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Принять заявку в друзья")
                        }
                        Button(
                            onClick = viewModel::rejectIncomingRequest,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Отклонить заявку в друзья")
                        }
                    }
                }

                FriendRequestStatusUi.Rejected -> {
                    Button(
                        onClick = viewModel::acceptIncomingRequest,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Принять заявку в друзья")
                    }
                }

                FriendRequestStatusUi.Done -> {
                    Button(
                        onClick = viewModel::removeFriend,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Удалить из друзей")
                    }
                }

                else -> {}
            }
        } else {
            Button(
                onClick = viewModel::changeRequestStatus,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                val requestStatus by viewModel.outgoingRequestStatus.collectAsState()
                when (requestStatus) {
                    FriendRequestStatusUi.None -> {
                        Text("Добавить в друзья")
                    }

                    FriendRequestStatusUi.Pending -> {
                        Text("Отменить заявку в друзья")
                    }

                    FriendRequestStatusUi.Done -> {
                        Text("Удалить из друзей")
                    }

                    FriendRequestStatusUi.Rejected -> {
                        Text("Отменить заявку в друзья")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val wantToGoEvents by viewModel.wantToGoEvents.collectAsState()
        EventSection("Хочет пойти", wantToGoEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))

        val goingEvents by viewModel.goingToEvents.collectAsState()
        EventSection("Идет", goingEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))

        val notGoingEvents by viewModel.notGoingToEvents.collectAsState()
        EventSection("Не пойдет", notGoingEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))
    }
}