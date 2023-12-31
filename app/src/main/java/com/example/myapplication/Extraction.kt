package com.example.myapplication

import android.util.Log
import androidx.core.text.isDigitsOnly

object Extraction {
    private const val TAG = "Extraction"
    var valueList = mutableListOf<String>()
    operator fun invoke(value: String): CardDetail {
        val list = value.split("\n").toMutableList()
        valueList = list
        Log.i(TAG, "invoke: list: $list")

        val cardDetail = CardDetail()

        cardDetail.expirationDate = extractExpirationDate()
        cardDetail.cvv2 = extractCvv2()
        cardDetail.cardNumber = extractNumbers()

        return cardDetail
    }

    private fun extractNumbers(): String {
        return try {
            Log.i(TAG, "extractNumbers: va $valueList")
            val target = valueList.map {
                it.replace(" ", "")
                    .replace(",", "")
            }.filter { it.isDigitsOnly() }
            Log.i(TAG, "extractNumbers: target: $target")

            var cardNumber = target.find { it.length == 16 }
            Log.i(TAG, "extractNumbers: card number: $cardNumber")
            cardNumber ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun extractCvv2(): String {
        return try {
            val target = valueList.find {
                (it.contains("c", true) and it.hasDigit()) or
                        (it.contains("v", true) and it.hasDigit()) or
                        (it.contains("w", true) and it.hasDigit())
            } ?: ""
            Log.i(TAG, "extractCvv2: target: $target")

            var cvv2List = listOf<String>()
            if (cvv2List.contains(" "))
                cvv2List = target.split(" ") // cvv2: 422
            else
                cvv2List = target.split(":") // cvv2:422

            Log.i(TAG, "extractCvv2: cvv2List: $cvv2List")
            val digitTarget = cvv2List.filter { it.isDigitsOnly() }

            Log.i(TAG, "extractCvv2: cvv2: ${digitTarget.lastOrNull()}")

            valueList.remove(target)
            digitTarget.lastOrNull() ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun extractExpirationDate(): String {
        return try {
            val target = valueList.find {
                it.contains("/")
            } ?: ""
            Log.i(TAG, "extractExpirationDate: target: $target")

            val splashIndex = target.indexOf('/')
            val leftSide = target.substring(0, splashIndex).filter { it.isDigit() }
            val rightSide = target.substring(splashIndex + 1, target.length).filter { it.isDigit() }

            Log.i(TAG, "extractExpirationDate: left: $leftSide right: $rightSide")

            var year = ""
            var month = ""

            if (rightSide.length == 2)
                month = rightSide

            if (leftSide.length == 2)
                year = leftSide
            else if (leftSide.length >= 2)
                year = leftSide.takeLast(2)


            Log.i(TAG, "extractExpirationDate: year: $year month: $month")

            valueList.remove(target)
            "$year/$month"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}

fun String.hasDigit(): Boolean {
    Log.i("TAG", "hasDigit: source: $this")
    val returnValue = false
    val numbers = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).map { it.toString() }
    for (char: Char in this) {
        numbers.forEach {
            if (it == char.toString())
                return true
        }

    }
    return false
}