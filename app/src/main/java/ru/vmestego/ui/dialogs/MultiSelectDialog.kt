package ru.vmestego.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> MultiSelectDialog(
    title: String = "Выбор",
    options: List<T>,
    initiallySelected: List<T> = emptyList(),
    optionLabel: (T) -> String = { it.toString() },
    onDismiss: () -> Unit,
    onDone: (List<T>) -> Unit
) {
    val selectedItems = remember { mutableStateListOf<T>().apply { addAll(initiallySelected) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDone(selectedItems)
                onDismiss()
            }) {
                Text("Готово")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                options.forEach { item ->
                    val isSelected = item in selectedItems
                    Row(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (isSelected) selectedItems.remove(item)
                                    else selectedItems.add(item)
                                }
                            ),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.Companion.width(8.dp))
                        Text(optionLabel(item))
                    }
                }
            }
        }
    )
}