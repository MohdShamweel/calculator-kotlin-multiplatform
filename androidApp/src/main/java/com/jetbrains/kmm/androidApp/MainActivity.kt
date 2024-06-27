package com.jetbrains.kmm.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetbrains.kmm.androidApp.components.UserBar
import com.jetbrains.kmm.androidApp.screen.LoginScreen
import com.jetbrains.kmm.shared.enums.Screen
import com.jetbrains.kmm.shared.Calculator
import com.jetbrains.kmm.shared.manager.AuthManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authManager = AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val startScreen =
                remember { if (authManager.isLoggedIn()) Screen.CalculatorScreen else Screen.LoginScreen }
            var screen by remember { mutableStateOf(startScreen) }
            val user by authManager.user.collectAsState()
            when (screen) {
                Screen.LoginScreen -> {
                    LoginScreen(
                        onLogin = {
                            coroutineScope.launch {
                                authManager.login(
                                    idToken = it,
                                    onSuccess = {
                                        screen = Screen.CalculatorScreen
                                    }
                                )
                            }
                        }
                    )
                }

                Screen.CalculatorScreen -> {
                    Column(Modifier.background(Color.Black)) {
                        if (user != null) {
                            UserBar(
                                user = user,
                                onLogout = {
                                    coroutineScope.launch {
                                        authManager.logout()
                                        screen = Screen.LoginScreen
                                    }
                                }
                            )
                        }
                        CalculatorApp()
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var displayValue by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf<Int?>(null) }
    var secondNumber by remember { mutableStateOf<Int?>(null) }
    var currentOperation by remember { mutableStateOf<String?>(null) }

    val buttonValues = listOf(
        listOf("AC", "+/-", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = displayValue,
            color = Color.White,
            fontSize = 72.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth(),
            maxLines = 1
        )

        buttonValues.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { button ->
                    CalculatorButton(
                        label = button,
                        onClick = { buttonTapped(button, displayValue, currentOperation, firstNumber, secondNumber) { newDisplayValue, newFirstNumber, newSecondNumber, newCurrentOperation ->
                            displayValue = newDisplayValue
                            firstNumber = newFirstNumber
                            secondNumber = newSecondNumber
                            currentOperation = newCurrentOperation
                        } }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = when (label) {
                "AC", "+/-", "%" -> Color.Gray
                "÷", "×", "-", "+", "=" -> Color(0xFFFF9500)
                else -> Color(0xFF333333)
            }
        ),
        modifier = Modifier
            .size(if (label == "0") 164.dp else 80.dp)
    ) {
        Text(
            text = label,
            fontSize = 32.sp,
            color = Color.White
        )
    }
}

fun buttonTapped(
    button: String,
    displayValue: String,
    currentOperation: String?,
    firstNumber: Int?,
    secondNumber: Int?,
    onUpdate: (String, Int?, Int?, String?) -> Unit
) {
    when (button) {
        "AC" -> onUpdate("0", null, null, null)
        "+/-" -> {
            val newValue = if (displayValue.startsWith("-")) {
                displayValue.drop(1)
            } else {
                "-$displayValue"
            }
            onUpdate(newValue, firstNumber, secondNumber, currentOperation)
        }
        "%" -> {
            val value = displayValue.toDoubleOrNull() ?: 0.0
            onUpdate((value / 100).toString(), firstNumber, secondNumber, currentOperation)
        }
        "÷", "×", "-", "+" -> {
            if (firstNumber == null) {
                onUpdate("0", displayValue.toIntOrNull(), null, button)
            } else {
                onUpdate(displayValue, firstNumber, displayValue.toIntOrNull(), button)
            }
        }
        "=" -> {
            if (firstNumber != null && currentOperation != null) {
                val second = displayValue.toIntOrNull() ?: 0
                val result = when (currentOperation) {
                    "+" -> Calculator.sum3(firstNumber, second)
                    "-" -> Calculator.minus(firstNumber, second)
                    "×" -> Calculator.multiply(firstNumber, second)
                    "÷" -> Calculator.divide(firstNumber, second)
                    else -> 0
                }
                onUpdate(result.toString(), null, null, null)
            }
        }
        else -> {
            if (displayValue == "0" || currentOperation != null) {
                onUpdate(button, firstNumber, secondNumber, currentOperation)
            } else {
                onUpdate(displayValue + button, firstNumber, secondNumber, currentOperation)
            }
        }
    }
}
