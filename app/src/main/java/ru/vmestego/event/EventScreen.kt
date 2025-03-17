package ru.vmestego.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vmestego.R
import ru.vmestego.generateWarmSoftColor
import ru.vmestego.utils.LocalDateFormatters
import java.time.LocalDate

@Composable
fun EventScreenWrapper(eventUi: EventUi, goBackToSearch: () -> Unit) {
    val showBottomSheet = remember { mutableStateOf(false) }

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
                        text = "2",
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
    }) { innerPadding ->
        EventScreen(eventUi, innerPadding, goBackToSearch, showBottomSheet)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    eventUi: EventUi,
    innerPadding: PaddingValues,
    goBackToSearch: () -> Unit,
    showBottomSheet: MutableState<Boolean>
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
            Text(
                eventUi.eventName,
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, bottom = 5.dp),
                color = Color.White,
                fontSize = 36.sp
            )

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Hello")
            }
        }
    }
}

@Preview
@Composable
fun EventScreenPreview() {
    EventScreenWrapper(
        EventUi(
            eventName = "Икар",
            locationName = "КЗ Измайлово",
            date = LocalDate.now(),
            description = "В мире «Икара» около 50 лет назад произошла глобальная война. Применялось биологическое оружие, которое уничтожило взрослое население, и в живых остались только дети и подростки. При этом они частично потеряли память. События происходят в период, когда на руинах цивилизации поднялся Полис — город, возведенный на основе секретной военной базы. Он закрыт защитным Куполом и концентрирует в себе все ресурсы и технологии, собранные из разрушенного мира. Полис процветает. Все, кто вне Полиса — с трудом выживают..."
        ),
        {}
    )
}


