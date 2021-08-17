package com.KotlinNinja.bookhub.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao   // telling compiler it is Dao
interface BookDao {
    @Insert   // annotating
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM books")
    fun getAllBooks():List<BookEntity>

    @Query("SELECT * FROM books WHERE book_id= :bookId")
    fun getBookId(bookId:String):BookEntity


    // above only have declaration and no function body
    // that is why interface is used here
}