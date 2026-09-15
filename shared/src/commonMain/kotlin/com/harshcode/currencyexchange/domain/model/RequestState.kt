package com.harshcode.currencyexchange.domain.model

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/*
      * When we try to fetch data, we need to make sure to handle error case as well, So I have made
        this custom wrapper class, so that I can properly format a response in UI later.
      * Created a RequestState sealed class, this will be a generic class, and we are gonna have
        4 different states: Idle, Loading, Success and Error.
      * The first 2 Idle and Loading are not holding any data.
      * While Success will have a generic type.
      * And Error a message of String type.

      * I have also added some utility functions to return a boolean value based on the current state.

      * I have also added 2 more function to extract the generic data from success state and error
        message from error state. (Keep In mind that these 2 functions can throw exceptions if you
        don't check the current state of the RequestState before that.)
 */


sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val errorMessage: String) : RequestState<Nothing>()

    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    fun getSuccessData() = (this as Success).data
    fun getError() = (this as Error).errorMessage
}

@Composable
fun <T> RequestState<T>.DisplayResult(
    onIdle: (@Composable () -> Unit)? = null,
    onLoading: (@Composable () -> Unit)? = null,
    onError: (@Composable (String) -> Unit)? = null,
    onSuccess: @Composable (T) -> Unit,
    transitionSpec: ContentTransform = scaleIn(tween(durationMillis = 400))
            + fadeIn(tween(durationMillis = 800))
            togetherWith scaleOut(tween(durationMillis = 400))
            + fadeOut(tween(durationMillis = 800))
) {
    AnimatedContent(
        targetState = this,
        transitionSpec = { transitionSpec },
        label = "Content Animation",
    ) { state ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (state) {
                RequestState.Idle -> {
                    onIdle?.invoke()
                }
                RequestState.Loading -> {
                    onLoading?.invoke()
                }
                is RequestState.Error -> {
                    onError?.invoke(state.errorMessage)
                }
                is RequestState.Success<*> -> {
                    onSuccess.invoke(state.getSuccessData())
                }
            }

        }
    }
}