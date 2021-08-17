package com.KotlinNinja.bookhub.model
//these class also known as models
data class Book(
    val bookId:String,
    val bookName:String,
    val bookAuthor:String,
    val bookRating:String,
    val bookPrice:String,
    val bookImage: String   //Since it holds the ID of the image
)
