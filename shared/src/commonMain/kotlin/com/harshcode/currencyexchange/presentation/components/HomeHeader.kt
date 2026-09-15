package com.harshcode.currencyexchange.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.CurrencyCode
import com.harshcode.currencyexchange.domain.model.CurrencyType
import com.harshcode.currencyexchange.domain.model.DisplayResult
import com.harshcode.currencyexchange.domain.model.RateStatus
import com.harshcode.currencyexchange.domain.model.RequestState
import com.harshcode.currencyexchange.ui.headerColor
import com.harshcode.currencyexchange.ui.staleColor
import com.harshcode.currencyexchange.util.displayCurrentDateTime
import currencyexchange.shared.generated.resources.Res
import currencyexchange.shared.generated.resources.exchange_illustration
import currencyexchange.shared.generated.resources.refresh_ic
import currencyexchange.shared.generated.resources.switch_ic
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeHeader(
    status: RateStatus,
    onRatesRefreshed: () -> Unit,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit,
    amount: Double,
    onAmountChanged: (Double) -> Unit,
    onCurrencyTypeSelected: (CurrencyType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RatesStatus(status = status, onRatesRefreshed = onRatesRefreshed)
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClick = onSwitchClick,
            onCurrencyTypeSelected = onCurrencyTypeSelected
        )
        Spacer(modifier = Modifier.height(24.dp))
        AmountInput(
            amount = amount,
            onAmountChanged = onAmountChanged
        )
    }
}

@Composable
fun RatesStatus(
    status: RateStatus,
    onRatesRefreshed: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(Res.drawable.exchange_illustration),
            contentDescription = ""
        )
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayCurrentDateTime(),
                color = Color.White
            )
            Text(
                text = status.title,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = status.color
            )
        }
        if (status == RateStatus.Stale) {
            IconButton(onClick = onRatesRefreshed) {
                Icon(
                    painter = painterResource(Res.drawable.refresh_ic),
                    contentDescription = "Refresh Icon",
                    modifier = Modifier.size(20.dp),
                    tint = staleColor
                )
            }
        } else {
            Box(modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit,
    onCurrencyTypeSelected: (CurrencyType) -> Unit
) {

    var animationStarted by remember {mutableStateOf(false)}
    val animateRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyView(
            placeHolder = "From",
            currency = source,
            onClick = {
                if (source.isSuccess()){
                    onCurrencyTypeSelected(
                        CurrencyType.Source(
                            currencyCode = CurrencyCode.valueOf(source.getSuccessData().code)
                        )
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(14.dp))
        IconButton(
            modifier = Modifier.offset(y = 10.dp)
                .graphicsLayer(
                    rotationY = animateRotation
                ),

            onClick = {
                animationStarted = !animationStarted
                onSwitchClick()
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch Icon",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        CurrencyView(
            placeHolder = "To",
            currency = target,
            onClick = {
                if (target.isSuccess()){
                    onCurrencyTypeSelected(
                        CurrencyType.Target(
                            currencyCode = CurrencyCode.valueOf(target.getSuccessData().code)
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun RowScope.CurrencyView(
    placeHolder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = placeHolder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .height(54.dp)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            currency.DisplayResult(
                onLoading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 1.dp
                    )
                },
                onSuccess = {currency ->
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(CurrencyCode.valueOf(currency.code).flag),
                        contentDescription = "Flag Icon",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CurrencyCode.valueOf(currency.code).name,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White
                    )
                }
            )
        }
    }
}

@Composable
fun AmountInput(
    amount: Double,
    onAmountChanged: (Double) -> Unit
) {
    TextField(
        value = "$amount",
        onValueChange = {
            it.toDoubleOrNull()?.let { value ->
                onAmountChanged(value)
            }
        },
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Enter amount",
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .animateContentSize()
            .height(54.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.White.copy(alpha = 0.05f),
            errorContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}
