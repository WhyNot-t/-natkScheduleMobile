package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    selectedGroup: String,
    onSelectedGroupChange: (String) -> Unit,
    favoriteGroups: List<String>,
    onToggleFavorite: (String) -> Unit,
) {

    var groups by remember { mutableStateOf<List<String>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    var schedule by remember {
        mutableStateOf<List<ScheduleByDateDto>>(emptyList())
    }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            groups = RetrofitInstance.api
                .getGroups()
                .map { it.groupName }
                .distinct()
                .sorted()
        } catch (_: Exception) {
            groups = listOf(selectedGroup)
        }
    }

    LaunchedEffect(selectedGroup) {
        val (start, end) = getWeekDateRange()
        loading = true
        error = null
        try {
            schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
        } catch (e: Exception) {
            error = e.message
            schedule = emptyList()
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedGroup,
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    label = { Text("Группа") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    singleLine = true,
                    readOnly = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (groups.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Список групп пуст") },
                            onClick = {}
                        )
                    } else {
                        groups.forEach { group ->
                            val favorite = favoriteGroups.contains(group)
                            DropdownMenuItem(
                                text = { Text(group) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    onSelectedGroupChange(group)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            IconButton(onClick = { onToggleFavorite(selectedGroup) }) {
                val isFavorite = favoriteGroups.contains(selectedGroup)
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Добавить в избранное"
                )
            }
        }

        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Ошибка: $error")
            else -> ScheduleList(schedule)
        }
    }
}