package ru.vmestego.ui.mainActivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vmestego.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreenWrapper(viewModel: OtherUserProfileViewModel) {
    val state = rememberPullToRefreshState()

    if (viewModel.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = viewModel.isLoading,
            onRefresh = viewModel::getRequestStatus,
            state = state
        ) {
            OtherUserProfileScreen(viewModel)
        }
    }
}

@Composable
fun OtherUserProfileScreen(viewModel: OtherUserProfileViewModel) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = viewModel.username,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = viewModel::changeRequestStatus,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            when (viewModel.requestStatus) {
                RequestStatus.NONE -> {
                    Text("Добавить в друзья")
                }

                RequestStatus.PENDING -> {
                    Text("Отменить заявку в друзья")
                }

                RequestStatus.DONE -> {
                    Text("Удалить из друзей")
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Хочет пойти",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Идет",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Не пойдет",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
    }
}