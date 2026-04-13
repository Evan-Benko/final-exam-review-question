package com.mad411.habittracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mad411.habittracker.viewmodel.QuoteViewModel

//screen that shows a random motivational quote from the api
//the UI reacts to the sealed class state, one of loading / success / error

@Composable
fun QuoteScreen(viewModel: QuoteViewModel = viewModel()) {

    //grab the current state, will recompose any time the viewmodel changes it
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // when block on the sealed class, kotlin smart casts inside each branch
        when (val s = state) {
            is QuoteViewModel.QuoteUiState.Loading -> {
                //spinner while we wait for the network
                CircularProgressIndicator()
                Text("loading quote...")
            }

            is QuoteViewModel.QuoteUiState.Success -> {
                //quote itself, bigger and italic for flair
                Text(
                    text = "\"${s.text}\"",
                    fontStyle = FontStyle.Italic
                )
                //author below
                Text(text = "- ${s.author}")
            }

            is QuoteViewModel.QuoteUiState.Error -> {
                Text(text = "Error: ${s.message}")
            }
        }

        //refresh button, always visible so you can retry on error too
        Button(
            onClick = { viewModel.fetchNewQuote() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show New Quote")
        }
    }
}
