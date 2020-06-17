package com.example.collegetrade.data

// TODO: Change timestamp to Long type

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
    var timestamp: String = "",
    var dataPosted: String = "",
    var viewsCount: Int = 0,
    var likesCount: Int = 0,
    var isPremium: Boolean = false
) {
    override fun toString(): String {
        return "Ad(id='$id', sellerName='$sellerName', sellerId='$sellerId', category='$category', subCategory='$subCategory', title='$title', description='$description', image='$image', price='$price', timestamp=$timestamp, dataPosted='$dataPosted', viewsCount=$viewsCount, likesCount=$likesCount, isPremium=$isPremium)"
    }
}