package com.jetbrains.kmm.shared.enums

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object CalculatorScreen : Screen("calculator_screen")
}