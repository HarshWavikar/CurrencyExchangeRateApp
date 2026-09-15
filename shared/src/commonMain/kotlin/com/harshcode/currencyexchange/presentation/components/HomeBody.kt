package com.harshcode.currencyexchange.presentation.components


import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.CurrencyCode
import com.harshcode.currencyexchange.domain.model.RequestState
import com.harshcode.currencyexchange.ui.headerColor
import com.harshcode.currencyexchange.util.DoubleConverter
import com.harshcode.currencyexchange.util.GetBebasFontFamily
import com.harshcode.currencyexchange.util.calculateExchangeRate
import com.harshcode.currencyexchange.util.convert
import com.harshcode.currencyexchange.util.round
import com.harshcode.currencyexchange.util.wavingFlag
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeBody(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double,
) {
    var exchangedAmount by rememberSaveable { mutableStateOf(0.0) }

    val animateExchangeAmount by animateValueAsState(
        targetValue = exchangedAmount,
        animationSpec = tween(durationMillis = 1000),
        typeConverter = DoubleConverter()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            if (source.isSuccess() && target.isSuccess()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .wavingFlag(),
                            painter = painterResource(CurrencyCode.valueOf(source.getSuccessData().code).fullFlag),
                            contentDescription = "Flag Icon",
                        )

                        Text(
                            modifier = Modifier.offset(y= (-10).dp),
                            text = CurrencyCode.valueOf(source.getSuccessData().code).country,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .wavingFlag(),
                            painter = painterResource(CurrencyCode.valueOf(target.getSuccessData().code).fullFlag),
                            contentDescription = "Flag Icon",
                        )
                        Text(
                            text = CurrencyCode.valueOf(target.getSuccessData().code).country,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = amount.toString() + " " + CurrencyCode.valueOf(source.getSuccessData().code).symbol,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontFamily = GetBebasFontFamily(),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = " = ",
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${animateExchangeAmount.round(2)} ${CurrencyCode.valueOf(target.getSuccessData().code).symbol} ",
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontFamily = GetBebasFontFamily(),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Column {
                    val sourceRate = source.getSuccessData().value
                    val targetRate = target.getSuccessData().value

                    Box() {

                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "1 ${source.getSuccessData().code} =" +
                                " ${calculateExchangeRate(sourceRate, targetRate).round(4)} " +
                                target.getSuccessData().code,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (isSystemInDarkTheme())
                            Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "1 ${target.getSuccessData().code} =" +
                                " ${calculateExchangeRate(targetRate, sourceRate).round(4)} " +
                                source.getSuccessData().code,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (isSystemInDarkTheme())
                            Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 24.dp)
                .background(
                    color = Color.Unspecified,
                    shape = RoundedCornerShape(99.dp)
                ),
            onClick = {
                if (source.isSuccess() && target.isSuccess()) {
                    val exchangeRate = calculateExchangeRate(
                        source = source.getSuccessData().value,
                        target = target.getSuccessData().value
                    )
                    exchangedAmount = convert(
                        amount = amount,
                        exchangeRate = exchangeRate
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = headerColor
            )
        ) {
            Text(
                text = "Convert"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}