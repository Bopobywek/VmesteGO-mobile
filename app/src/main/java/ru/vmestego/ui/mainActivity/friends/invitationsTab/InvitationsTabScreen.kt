package ru.vmestego.ui.mainActivity.friends.invitationsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ru.vmestego.R
import ru.vmestego.ui.mainActivity.noRippleClickable
import ru.vmestego.utils.LocalDateTimeFormatters
import kotlin.math.min

@Composable
fun InvitationsTabScreen(viewModel: InvitationsTabViewModel = viewModel()) {
    var showBottomSheet = remember { mutableStateOf(false) }

    if (showBottomSheet.value) {
        OutgoingInvitationsModal(
            showBottomSheet,
            viewModel.sentInvitations.collectAsState().value,
            { viewModel.cancelInvitation(it) })
    }
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
                        text = viewModel.sentInvitations.collectAsState().value.size.toString(),
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Входящие",
            Modifier.padding(start = 20.dp),
            color = Color.Companion.DarkGray
        )
        Spacer(Modifier.height(2.dp))
        HorizontalDivider(Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
        val pendingInvitations by viewModel.pendingInvitations.collectAsState()
        if (pendingInvitations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(
                        "У вас пока нет приглашений",
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            InvitationsList(pendingInvitations, viewModel::acceptInvite, viewModel::rejectInvite)
        }
    }
}

@Composable
fun InvitationsList(
    pendingInvitations: List<InvitationUi>,
    acceptInvite: (List<InvitationUi>) -> Unit,
    rejectInvite: (List<InvitationUi>) -> Unit
) {
    val profileImageSize = 32
    val boxContentPadding = 15

    val grouped = pendingInvitations.groupBy { it.event.id }.toList()
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(grouped) {
            val eventInfo = it.second[0].event
            val users = it.second.map { it.sender }

            Column {
                Box(
                    Modifier.padding(15.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .heightIn(min = 100.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Column {
                                Text(
                                    text = eventInfo.eventName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = LocalDateTimeFormatters.formatByDefault(eventInfo.dateTime),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Place, contentDescription = "Location")
                                Text(
                                    text = eventInfo.locationName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(20.dp))


                        }
                    }

                    val maxImages = 5
                    Row(
                        modifier = Modifier
                            .offset(x = boxContentPadding.dp, y = (profileImageSize / 2).dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(30.dp))
                            .noRippleClickable { }
                    ) {
                        for (i in 0..min(users.size - 1, maxImages - 1)) {
                            AsyncImage(
                                model = users[i].imageUrl,
                                error = painterResource(R.drawable.ic_launcher_background),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .offset(x = (-i * profileImageSize / 2).dp)
                                    .size(profileImageSize.dp)
                                    .clip(CircleShape)
                            )
                        }

                        if (users.size >= maxImages) {
                            val remain = users.size - maxImages
                            Box(
                                modifier = Modifier
                                    .offset(x = (-maxImages * profileImageSize / 2).dp)
                                    .size(profileImageSize.dp)
                                    .clip(CircleShape)
                                    // fff5ee
                                    .background(Color(1f, 0.9607843137254902f, 0.9333f, 1f))
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = "+$remain",
                                    fontSize = 14.sp,
                                    // 696969
                                    color = Color(
                                        0.4117647058823529f,
                                        0.4117647058823529f,
                                        0.4117647058823529f
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    Button({ acceptInvite(it.second) }) {
                        Text("Принять")
                    }
                    OutlinedButton({ rejectInvite(it.second) }) {
                        Text("Отклонить")
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(Modifier.padding(horizontal = 30.dp), thickness = 2.dp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutgoingInvitationsModal(
    showModal: MutableState<Boolean>,
    sentInvitations: List<InvitationUi>,
    cancelInvitation: (InvitationUi) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            showModal.value = false
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            for (invitation in sentInvitations) {
                ElevatedCard(
                    modifier = Modifier
                        .heightIn(min = 100.dp)
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    Box(Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { cancelInvitation(invitation) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Column {
                                Text(
                                    text = invitation.event.eventName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = LocalDateTimeFormatters.formatByDefault(invitation.event.dateTime),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Place, contentDescription = "Location")
                                Text(
                                    text = invitation.event.locationName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Отправлено пользователю",
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = invitation.receiver.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

