package com.harshcode.currencyexchange.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.CurrencyCode
import com.harshcode.currencyexchange.domain.model.RequestState
import com.harshcode.currencyexchange.ui.headerColor
import com.harshcode.currencyexchange.ui.primaryColor
import com.harshcode.currencyexchange.util.DoubleConverter
import com.harshcode.currencyexchange.util.ExchangeRateText
import com.harshcode.currencyexchange.util.GetBebasFontFamily
import com.harshcode.currencyexchange.util.calculateExchangeRate
import com.harshcode.currencyexchange.util.convert
import com.harshcode.currencyexchange.util.round
import com.harshcode.currencyexchange.util.toCleanString
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
                val sourceData = source.getSuccessData()
                val targetData = target.getSuccessData()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    AnimatedContent(
                        targetState = sourceData.code,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(durationMillis = 400)) + fadeIn(
                                animationSpec = tween(durationMillis = 400)
                            ) togetherWith
                                    scaleOut(animationSpec = tween(durationMillis = 400)) + fadeOut(
                                animationSpec = tween(durationMillis = 400)
                            )
                        },
                        label = "Source Flag Animation"
                    ) { sourceCode ->
                        Column(
                            modifier = Modifier
                                .width(155.dp)
                                .height(150.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                maxLines = 2,
                                lineHeight = 18.sp,
                                text = CurrencyCode.valueOf(sourceCode).country,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Image(
                                modifier = Modifier
                                    .size(150.dp)
                                    .wavingFlag(),
                                painter = painterResource(CurrencyCode.valueOf(sourceCode).fullFlag),
                                contentDescription = "Flag Icon",
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = targetData.code,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(durationMillis = 400)) + fadeIn(
                                animationSpec = tween(durationMillis = 400)
                            ) togetherWith
                                    scaleOut(animationSpec = tween(durationMillis = 400)) + fadeOut(
                                animationSpec = tween(durationMillis = 400)
                            )
                        },
                        label = "Target Flag Animation"
                    ) { targetCode ->
                        Column(
                            modifier = Modifier
                                .width(155.dp)
                                .height(150.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                maxLines = 2,
                                lineHeight = 18.sp,
                                text = CurrencyCode.valueOf(targetCode).country,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Image(
                                modifier = Modifier
                                    .size(150.dp)
                                    .wavingFlag(),
                                painter = painterResource(CurrencyCode.valueOf(targetCode).fullFlag),
                                contentDescription = "Flag Icon",
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column (
                        modifier = Modifier.weight(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = amount.toString() + " " + CurrencyCode.valueOf(source.getSuccessData().code).symbol,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontFamily = GetBebasFontFamily(),
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = CurrencyCode.valueOf(source.getSuccessData().code).currencyName,
                            fontSize = MaterialTheme.typography.bodyMediumEmphasized.fontSize,
                            maxLines = 2,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        modifier = Modifier.weight(0.1f),
                        text = " = ",
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Column (
                        modifier = Modifier.weight(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "${animateExchangeAmount.round(2)} ${CurrencyCode.valueOf(target.getSuccessData().code).symbol} ",
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontFamily = GetBebasFontFamily(),
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = CurrencyCode.valueOf(target.getSuccessData().code).currencyName,
                            fontSize = MaterialTheme.typography.bodyMediumEmphasized.fontSize,
                            maxLines = 2,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSystemInDarkTheme())
                                Color.White.copy(0.05f) else Color.Black.copy(0.05f)
                        )
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    val sourceRate = source.getSuccessData().value
                    val targetRate = target.getSuccessData().value

                    ExchangeRateText(
                        text = "1 ${source.getSuccessData().code} =" +
                                " ${
                                    calculateExchangeRate(
                                        source = sourceRate,
                                        target = targetRate
                                    ).toCleanString(4)
                                } " + target.getSuccessData().code
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExchangeRateText(
                        text = "1 ${target.getSuccessData().code} =" +
                                " ${
                                    calculateExchangeRate(
                                        source = targetRate,
                                        target = sourceRate
                                    ).toCleanString(4)
                                } " + source.getSuccessData().code
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