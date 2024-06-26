package com.jetbrains.kmm.androidApp.screen

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object CalculatorScreen : Screen("calculator_screen")
}