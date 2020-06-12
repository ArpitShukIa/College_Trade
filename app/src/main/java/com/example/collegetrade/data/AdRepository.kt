package com.example.collegetrade.data

interface AdRepository {

    suspend fun postAd(ad: Ad)

}