package com.mad411.habittracker.network

// data class that matches the JSON shape from zenquotes.io/api/random
// gson will auto map these fields based on the json keys
// the api returns a list with one item, each item looks like:
// { "q": "the quote text", "a": "author name", "h": "html version" }

data class Quote(
    //q is the quote text
    val q: String,
    //a is the author
    val a: String
)
