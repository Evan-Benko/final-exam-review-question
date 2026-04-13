package com.mad411.habittracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad411.habittracker.viewmodel.HabitViewModel

//screen for adding a new habit
// holds its own local state for the form fields then pushes to viewmodel on save
// @OptIn needed because TimePicker is still experimental in material3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onHabitSaved: () -> Unit,
    viewModel: HabitViewModel = viewModel()
) {
    //local form state, remember keeps it across recompositions
    //we dont need rememberSaveable here because once they save we nav away anyway
    //could argue for rememberSaveable if you want it to survive rotation on this screen
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    // time picker state, default to 8am
    val timeState = rememberTimePickerState(initialHour = 8, initialMinute = 0)

    //controls whether the time picker dialog is showing
    var showTimePicker by remember { mutableStateOf(false) }

    // formatted time string we display and save
    var timeString by remember { mutableStateOf("08:00") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add a New Habit")

        //habit name input, OutlinedTextField is the standard material input
        //state hoisting in action, the field doesnt own its value, we do
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        //button to pop the time picker dialog, shows the current selected time
        Button(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Time: $timeString")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // save button, disabled if name is blank so users cant save empty junk
            Button(
                onClick = {
                    viewModel.addHabit(name, goal, timeString)
                    //tell the parent we are done so it can pop the back stack
                    onHabitSaved()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }

    //time picker shows inside an alert dialog
    // material3 doesnt give us TimePickerDialog out of the box so we wrap it
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pick a time") },
            text = {
                TimePicker(state = timeState)
            },
            confirmButton = {
                TextButton(onClick = {
                    //format hour and minute with leading zeros so "08:05" not "8:5"
                    val h = timeState.hour.toString().padStart(2, '0')
                    val m = timeState.minute.toString().padStart(2, '0')
                    timeString = "$h:$m"
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
