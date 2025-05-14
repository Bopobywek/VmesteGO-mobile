package ru.vmestego.ui.mainActivity.tickets

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableItemWithActions(
    isRevealed: Boolean,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier.Companion,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var actionsWidth by remember {
        mutableFloatStateOf(0f)
    }
    var spacerMenuWidth by remember {
        mutableFloatStateOf(0f)
    }
    val offset = remember {
        Animatable(initialValue = 0f)
    }
    val scope = rememberCoroutineScope()

    var menuWidth = actionsWidth - spacerMenuWidth
    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            offset.animateTo(menuWidth)
        } else {
            offset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.Companion
                .onSizeChanged {
                    actionsWidth = it.width.toFloat()
                },
            verticalAlignment = Alignment.Companion.CenterVertically,
        ) {
            Spacer(
                Modifier.Companion
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Companion.Green)
                    .onSizeChanged {
                        spacerMenuWidth = it.width.toFloat()
                    })
            actions()
        }

        menuWidth = actionsWidth - spacerMenuWidth
        Surface(
            modifier = Modifier.Companion
                .fillMaxSize()
                .offset { -IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(menuWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offset.value - dragAmount)
                                    .coerceIn(0f, menuWidth)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value >= (menuWidth) / 2f -> {
                                    scope.launch {
                                        offset.animateTo(menuWidth)
                                        onExpanded()
                                    }
                                }

                                else -> {
                                    scope.launch {
                                        offset.animateTo(0f)
                                        onCollapsed()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}