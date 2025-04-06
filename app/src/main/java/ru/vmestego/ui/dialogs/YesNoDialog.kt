package ru.vmestego.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YesNoDialog(
    isDialogOpen: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { isDialogOpen.value = false },
        title = { Text(text = "Подтверждение действия") },
        text = { Text("Вы действительно хотите удалить выбранный элемент?") },
        confirmButton = {
            Button({
                isDialogOpen.value = false
                onConfirm()
            }, border = BorderStroke(1.dp, Color.LightGray)) {
                Text("Удалить", fontSize = 22.sp)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    isDialogOpen.value = false
                    onDismiss()
                },
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Отмена", fontSize = 22.sp)
            }
        },
        containerColor = Color.DarkGray,
        titleContentColor = Color.LightGray,
        textContentColor = Color.LightGray,
        iconContentColor = Color.LightGray
    )
}