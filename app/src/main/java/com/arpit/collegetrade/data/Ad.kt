package com.arpit.collegetrade.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ad(
    var id: String = "",
    var sellerName: String = "",
    var sellerId: String = "",
    var sellerPhoto: String = "",
    var category: String = "",
    var subCategory: String = "",
    var title: String = "",
    var description: String = "",
    var image: String = "",
    var price: String = "",
    var timestamp: Long = 0,
    var datePosted: String = "",
    var viewsCount: Int = 0,
    var likesCount: Int = 0,
    var isLiked: Boolean = false,
    var isPremium: Boolean = false,
    var likers: ArrayList<String> = ArrayList()
): Parcelable {
    override fun toString(): String {
        return "Ad(id='$id', sellerName='$sellerName', sellerId='$sellerId', sellerPhoto='$sellerPhoto', category='$category', subCategory='$subCategory', title='$title', description='$description', image='$image', price='$price', timestamp=$timestamp, datePosted='$datePosted', viewsCount=$viewsCount, likesCount=$likesCount, isLiked=$isLiked, isPremium=$isPremium, likers=$likers)"
    }
}