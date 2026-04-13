package com.mad411.habittracker.data

import java.util.concurrent.atomic.AtomicLong

// plain kotlin data class for a habit
// data class gives us equals, hashCode and copy for free
// we need equals for LazyColumn to know when items actually changed

data class Habit(
    // unique id, we use this as the stable key in LazyColumn
    // AtomicLong counter instead of currentTimeMillis() — multiple Habits created in
    // the same millisecond (e.g. loadDefaults) would get identical ids and crash LazyColumn.
    // Seeded at currentTimeMillis so ids never collide with values already saved to disk.
    val id: Long = idCounter.getAndIncrement(),
    val name: String,
    val goal: String,
    //storing time as a simple string like "08:30" to keep file io easy
    val time: String
) {
    companion object {
        private val idCounter = AtomicLong(System.currentTimeMillis())
    }
}
