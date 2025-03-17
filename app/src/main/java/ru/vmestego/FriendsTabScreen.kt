package ru.vmestego

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsTabScreen(viewModel: FriendsTabViewModel = viewModel()) {
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
                        text = "2",
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

        FriendsList(viewModel.users)
    }

    FriendsRequestsModalSheet(showBottomSheet)
}

@Composable
fun FriendsList(users: List<UserUi>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(users) { user ->
            Box(
                Modifier
                    .padding(horizontal = 20.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(15.dp))
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.Gray),
                        modifier = Modifier
                            .size(42.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsRequestsModalSheet(showBottomSheet: MutableState<Boolean>) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(generateWarmSoftColor()),
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "test",
                            fontWeight = FontWeight.W400,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            rememberVectorPainter(image = Icons.Filled.Done),
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(32.dp),
                            tint = { Color.Green })
                    }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}