package com.mad411.habittracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad411.habittracker.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//viewmodel for the quote screen
// handles the network call and exposes quote state to the UI
//using a sealed class for the UI state so we can represent loading, success, error cleanly

class QuoteViewModel : ViewModel() {

    //sealed class means QuoteUiState can only be one of these three types
    //compose can smart cast on these in a when block
    sealed class QuoteUiState {
        data object Loading : QuoteUiState()
        data class Success(val text: String, val author: String) : QuoteUiState()
        data class Error(val message: String) : QuoteUiState()
    }

    private val _uiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    init {
        //grab a quote as soon as the viewmodel gets created
        fetchNewQuote()
    }

    //called when the user hits the "show new quote" button
    fun fetchNewQuote() {
        _uiState.value = QuoteUiState.Loading

        // viewModelScope is tied to this viewmodels lifecycle
        // if the user navigates away and the screen dies, the coroutine gets cancelled
        // this is exactly the problem fragmentScope was solving in class
        viewModelScope.launch {
            try {
                //retrofit suspend call, auto runs on a background thread
                val quotes = RetrofitInstance.api.getRandomQuote()

                // zenquotes returns a list of 1, grab the first one
                val first = quotes.firstOrNull()
                if (first != null) {
                    _uiState.value = QuoteUiState.Success(
                        text = first.q,
                        author = first.a
                    )
                } else {
                    //api returned empty list somehow
                    _uiState.value = QuoteUiState.Error("No quote found")
                }
            } catch (e: Exception) {
                //network issue, bad json, whatever, show error state
                Log.e("QuoteViewModel", "fetch failed", e)
                _uiState.value = QuoteUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }
}
