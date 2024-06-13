package com.jetbrains.kmm.shared

class Calculator {
    companion object {
        fun sum(a: Int, b: Int): Int = a + b
        fun minus(a: Int, b: Int): Int = a - b
        fun multiply(a: Int, b: Int): Int = a * b
        fun divide(a: Int, b: Int): Int {
            if (b == 0) throw IllegalArgumentException("Divider can't be zero")
            return a / b
        }

        fun sum2(a: Int, b: Int): Int = a + b
        fun sum3(a: Int, b: Int): Int = a + b
    }
}
