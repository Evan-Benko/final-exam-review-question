package com.mad411.habittracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad411.habittracker.ui.components.HabitItem
import com.mad411.habittracker.viewmodel.HabitViewModel

// the main screen showing the list of habits
// takes lambdas for nav events so it stays decoupled from the nav graph
// this makes the screen easier to preview and test

@Composable
fun HabitListScreen(
    onAddHabitClick: () -> Unit,
    onShowQuoteClick: () -> Unit,
    // viewModel() here gives us the same viewmodel instance across recompositions
    // default param means test code can pass in a fake one if needed
    viewModel: HabitViewModel = viewModel()
) {
    //collectAsStateWithLifecycle is the lifecycle aware version
    //stops collecting when the screen isnt visible which saves battery
    val habits by viewModel.habits.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

        //title bar, keeping it simple without scaffold
        Text(
            text = "My Habits",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        //button row across the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onAddHabitClick, modifier = Modifier.weight(1f)) {
                Text("Add")
            }
            Button(
                onClick = { viewModel.loadDefaults() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Defaults")
            }
            Button(onClick = onShowQuoteClick, modifier = Modifier.weight(1f)) {
                Text("Quote")
            }
        }

        //if the list is empty show a message, otherwise show the LazyColumn
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No habits yet, tap Add or Defaults")
            }
        } else {
            // LazyColumn is the compose version of recyclerview
            // only composes whats on screen, same idea as viewholder recycling
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // key = habit.id is SUPER important, without it compose cant track items
                //properly across changes and animations will break
                //this is the equivalent of DiffUtil with a stable ID
                items(items = habits, key = { it.id }) { habit ->
                    HabitItem(habit = habit)
                }
            }
        }
    }
}
