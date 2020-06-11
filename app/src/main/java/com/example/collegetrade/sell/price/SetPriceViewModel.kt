package com.example.collegetrade.sell.price

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.collegetrade.sell.price.PriceState.*
import java.text.DecimalFormat

enum class PriceState { VALID, INVALID, EMPTY, NAVIGATE }

class SetPriceViewModel : ViewModel() {

    val price = MutableLiveData<String>()

    private val _priceState = MutableLiveData<PriceState>()
    val priceState: LiveData<PriceState> = _priceState

    val formattedPrice: LiveData<Boolean> = Transformations.map(price) {
        _priceState.value = VALID
        if (it.isNotEmpty()) {
            val num = it.replace(",", "").toInt()
            val newString = DecimalFormat("##,##,###").format(num)
            if (newString != it)
                price.value = newString
        }
        it.isNotEmpty()
    }

    fun navigate() {
        val amt = price.value!!
        _priceState.value = when {
            amt.isEmpty() -> EMPTY
            amt.length < 2 -> INVALID
            else -> NAVIGATE
        }
    }
}