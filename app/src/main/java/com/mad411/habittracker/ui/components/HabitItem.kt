package com.mad411.habittracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mad411.habittracker.data.Habit

// single habit row, STATELESS composable
// it just takes a habit in and shows it, doesnt own any state itself
// this is the state hoisting pattern we talked about in class
// stateless means it recomposes when its input changes but doesnt hold any memory of its own

@Composable
fun HabitItem(habit: Habit, modifier: Modifier = Modifier) {
    //Card gives us a nice elevated look for free, material3 component
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // habit name is the main title
            Text(
                text = habit.name,
                fontWeight = FontWeight.Bold
            )
            //row so goal and time sit on the same line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Goal: ${habit.goal}")
                Text(
                    text = habit.time,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
            }
        }
    }
}
