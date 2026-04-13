package com.mad411.habittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mad411.habittracker.data.Habit
import com.mad411.habittracker.data.HabitFileStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// this is the single source of truth for the habit list
// extends AndroidViewModel so we get application context for file io
// any screen that needs the habit list just observes this viewmodels state

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    //MutableStateFlow is the internal writable version, only the viewmodel can change it
    //we init with empty list, then load from file on startup
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())

    // expose a read only StateFlow to the UI, keeps outside code from messing with state directly
    //the compose screens collect this and recompose when it changes
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        //as soon as the viewmodel is created we try to load whatever was saved
        loadHabitsFromFile()
    }

    //add a new habit to the list and persist it
    fun addHabit(name: String, goal: String, time: String) {
        //guard against blank saves, simple validation
        if (name.isBlank()) return

        val newHabit = Habit(name = name, goal = goal, time = time)
        //update the state, compose will recompose anything observing habits
        val updated = _habits.value + newHabit
        _habits.value = updated
        //save in the background so we dont block the UI
        persistHabits(updated)
    }

    //fills the list with sample habits for the demo
    // hooked up to the "Load Defaults" button on the list screen
    fun loadDefaults() {
        val defaults = listOf(
            Habit(name = "Drink Water", goal = "8 glasses", time = "09:00"),
            Habit(name = "Meditate", goal = "10 minutes", time = "07:30"),
            Habit(name = "Read", goal = "20 pages", time = "21:00")
        )
        _habits.value = defaults
        persistHabits(defaults)
    }

    //kicks off reading the file on a background thread
    //viewModelScope auto cancels if the viewmodel dies so no leaks
    private fun loadHabitsFromFile() {
        viewModelScope.launch {
            // Dispatchers.IO is the right dispatcher for file access, NEVER use Main for this
            val loaded = withContext(Dispatchers.IO) {
                HabitFileStorage.loadHabits(getApplication())
            }
            _habits.value = loaded
        }
    }

    //same deal, write on IO thread
    private fun persistHabits(list: List<Habit>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HabitFileStorage.saveHabits(getApplication(), list)
            }
        }
    }
}
