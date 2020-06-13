package com.example.collegetrade.data

data class Ad(
    var id: String = "",
    var sellerName: String = "",
    var sellerId: String = "",
    var category: String = "",
    var subCategory: String = "",
    var title: String = "",
    var description: String = "",
    var image: String = "",
    var price: String = "",
    var dataPosted: String = "",
    var viewsCount: Int = 0,
    var likesCount: Int = 0,
    var isPremium: Boolean = false
) {
    override fun toString(): String {
        return "Ad(id='$id', sellerName='$sellerName', sellerId='$sellerId', category=$category, subCategory=$subCategory, title='$title', desc='$description', image='$image', price='$price', dataPosted='$dataPosted', viewsCount=$viewsCount, likesCount=$likesCount, isPremium=$isPremium)"
    }
}