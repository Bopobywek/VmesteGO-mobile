package ru.vmestego

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.vmestego.auth.AuthActivity

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val activity = LocalContext.current as Activity

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO: Handle notification click */ }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
            }
            IconButton(onClick = {
                viewModel.logout()
                activity.startActivity(Intent(activity, AuthActivity::class.java))
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
            }
        }

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
            text = "Вася Пупкин",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Хочу пойти",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Иду",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Не иду",
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
    }
}