package com.example.collegetrade.data

data class Ad(
    var id: String,
    var sellerName: String,
    var sellerId: String,
    var category: Int,
    var subCategory: Int,
    var title: String,
    var desc: String,
    var image: String,
    var price: String,
    var dataPosted: String,
    var viewsCount: Int,
    var likesCount: Int,
    var isPremium: Boolean
) {
    override fun toString(): String {
        return "Ad(id='$id', sellerName='$sellerName', sellerId='$sellerId', category=$category, subCategory=$subCategory, title='$title', desc='$desc', image='$image', price='$price', dataPosted='$dataPosted', viewsCount=$viewsCount, likesCount=$likesCount, isPremium=$isPremium)"
    }
}