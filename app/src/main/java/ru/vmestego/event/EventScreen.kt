package ru.vmestego.event

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.textfield.TextInputEditText
import ru.vmestego.R
import ru.vmestego.generateWarmSoftColor
import ru.vmestego.utils.LocalDateFormatters
import java.time.LocalDate

@Composable
fun EventScreenWrapper(eventUi: EventUi, goBackToSearch: () -> Unit) {
    val showBottomSheet = remember { mutableStateOf(false) }
    var comments = remember { mutableStateOf(listOf<Pair<String, String>>()) }
    Scaffold(bottomBar = {
        Button(
            onClick = {
                showBottomSheet.value = true
            },
            shape = RoundedCornerShape(10.dp),

            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 15.dp)
        )
        {
            Text("Обсуждение", fontSize = 16.sp)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
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
                        text = comments.value.size.toString(),
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
    }) { innerPadding ->
        EventScreen(eventUi, innerPadding, goBackToSearch, showBottomSheet, comments)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    eventUi: EventUi,
    innerPadding: PaddingValues,
    goBackToSearch: () -> Unit,
    showBottomSheet: MutableState<Boolean>,
    comments: MutableState<List<Pair<String, String>>>
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "",
                colorFilter = ColorFilter.tint(generateWarmSoftColor()),
                // https://developer.android.com/develop/ui/compose/graphics/images/customize
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, bottom = 5.dp),
                contentAlignment = Alignment.BottomStart
            ) {

                Text(
                    eventUi.eventName,
                    Modifier

                        .fillMaxWidth(),
                    color = Color.White,
                    fontSize = 36.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }

            Icon(
                rememberVectorPainter(image = Icons.Filled.Close),
                contentDescription = "Localized description",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(32.dp)
                    .clickable {
                        goBackToSearch()
                    },
                tint = { Color.White })
        }

        Column(
            Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 10.dp)
        ) {
            Text(LocalDateFormatters.formatByDefault(eventUi.date), fontSize = 16.sp)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(eventUi.locationName, fontSize = 24.sp)
                Icon(rememberVectorPainter(image = Icons.Filled.LocationOn),
                    contentDescription = "Localized description",
                    tint = { Color.Gray })
            }

            Spacer(Modifier.height(20.dp))

            SingleChoiceSegmentedButton(Modifier.fillMaxWidth(), eventUi)

            Spacer(Modifier.height(20.dp))

            Text("О мероприятии", fontSize = 20.sp)
            Spacer(Modifier.height(2.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(Modifier.height(5.dp))
            Text(eventUi.description, lineHeight = 18.sp)
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Box(
                Modifier.fillMaxSize()
            ) {
                var inputText by remember { mutableStateOf(TextFieldValue()) }
                Column(modifier = Modifier.imePadding()) {
                    comments.value.forEach { (user, text) ->
                        CommentItem(username = user, text = text)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Комментарий") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.7f)
                                .padding(8.dp)
                        )
                        IconButton(
                            onClick = {
                                if (inputText.text.isNotBlank()) {
                                    comments.value += ("andrey" to inputText.text)
                                    inputText = TextFieldValue()
                                }
                            },
                            modifier = Modifier
                                .background(Color.Transparent, RoundedCornerShape(10.dp))
                                .padding(top = 10.dp)
                        ) {
                            Icon(
                                rememberVectorPainter(image = Icons.AutoMirrored.Outlined.Send),
                                contentDescription = "Localized description",
                                tint = { Color.Black })
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    eventUi: EventUi,
    viewModel: EventViewModel = viewModel()
) {
    var selectedIndex by remember { mutableIntStateOf(2) }
    val options = listOf("Хочу пойти", "Иду", "Не иду")

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    viewModel.changeEventStatus(eventUi.id, index)
                },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun CommentItem(username: String, text: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = "@$username", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 10.dp))
        Spacer(modifier = Modifier.height(16.dp))

    }
}


@Preview
@Composable
fun EventScreenPreview() {
    EventScreenWrapper(
        EventUi(
            id = 1,
            eventName = "Икар",
            locationName = "КЗ Измайлово",
            date = LocalDate.now(),
            description = "В мире «Икара» около 50 лет назад произошла глобальная война. Применялось биологическое оружие, которое уничтожило взрослое население, и в живых остались только дети и подростки. При этом они частично потеряли память. События происходят в период, когда на руинах цивилизации поднялся Полис — город, возведенный на основе секретной военной базы. Он закрыт защитным Куполом и концентрирует в себе все ресурсы и технологии, собранные из разрушенного мира. Полис процветает. Все, кто вне Полиса — с трудом выживают..."
        ),
        {}
    )
}


