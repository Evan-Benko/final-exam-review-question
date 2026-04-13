package com.mad411.habittracker.data

import android.content.Context
import android.util.Log
import java.io.File

// this handles saving and loading habits to internal storage
// using a simple pipe seperated format instead of gson to keep it clear
// each line is one habit: id|name|goal|time

object HabitFileStorage {

    private const val TAG = "HabitFileStorage"
    //file lives in the apps internal storage so no permissions needed
    private const val FILE_NAME = "habits.txt"

    // save the full list, overwrites whatevers there
    // this MUST be called from a background thread, never from main
    fun saveHabits(context: Context, habits: List<Habit>) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            //build the whole file content as one string then write once
            val contents = habits.joinToString("\n") { h ->
                // using pipe as seperator, users wont likely type this in a habit name
                "${h.id}|${h.name}|${h.goal}|${h.time}"
            }
            file.writeText(contents)
            Log.d(TAG, "saved ${habits.size} habits")
        } catch (e: Exception) {
            //if writing fails we just log, app keeps running
            Log.e(TAG, "couldnt save habits", e)
        }
    }

    //read em back, returns empty list if file doesnt exist yet (first launch)
    fun loadHabits(context: Context): List<Habit> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) {
                Log.d(TAG, "no habit file yet, returning empty")
                return emptyList()
            }

            //read whole file, split on newlines, parse each line
            file.readLines()
                //skip blanks in case the file got weird
                .filter { it.isNotBlank() }
                .mapNotNull { line -> parseLine(line) }
                // guard: if a previous bug wrote duplicate ids, drop the dupes so
                // LazyColumn never receives two items with the same key
                .distinctBy { it.id }
        } catch (e: Exception) {
            Log.e(TAG, "couldnt load habits", e)
            emptyList()
        }
    }

    //parses a single line into a Habit, returns null if its malformed
    private fun parseLine(line: String): Habit? {
        val parts = line.split("|")
        //need exactly 4 parts, if not its corrupt so skip it
        if (parts.size != 4) return null
        return try {
            Habit(
                id = parts[0].toLong(),
                name = parts[1],
                goal = parts[2],
                time = parts[3]
            )
        } catch (e: NumberFormatException) {
            //id wasnt a valid long, skip this line
            null
        }
    }
}
