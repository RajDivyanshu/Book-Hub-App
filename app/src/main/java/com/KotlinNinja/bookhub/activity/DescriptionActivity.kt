package com.KotlinNinja.bookhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.KotlinNinja.bookhub.R
import com.KotlinNinja.bookhub.databse.BookDatabase
import com.KotlinNinja.bookhub.databse.BookEntity
import com.KotlinNinja.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class DescriptionActivity : AppCompatActivity() {


    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar


    var bookId: String? = "100"      //giving random value to bookId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility =
            View.VISIBLE       //visible as open this activity till the actual data loaded from the server
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility =
            View.VISIBLE      //visible as open this activity till the actual data loaded from the server

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)    // changing the title of toolbar by using this method
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {   //for resolving Json Exception error

                        val success =
                            it.getBoolean("success")  // Extracting the value from the response(success)
                        if (success) {     // condition for extracting data from json array
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(
                                applicationContext,
                                bookEntity,
                                1
                            ).execute() // execute() for start background process
                            // above is a object
                            val isFav = checkFav.get() //particular book is favourite or not
                            if (isFav) {
                                btnAddToFav.text = "Remove from Favourites"
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorFavourite
                                )
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                btnAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorPrimary
                                )
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            btnAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute().get()) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Added to Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Remove from Favourites"
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavourite
                                        )
                                        btnAddToFav.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result =  async.get()
                                    if (result){
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Removed from Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text= "Add to Favourites"
                                        val noFavColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                        ).show()
                    }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity, "Volley error $it", Toast.LENGTH_SHORT)
                        .show()
                }

                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "00d5a14c31a944"       //00d5a14c31a944 //9bf534118365f1
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog =
                AlertDialog.Builder(this@DescriptionActivity)   // AlertDialog(class)  Builder(subclass)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent =
                    Intent(Settings.ACTION_WIRELESS_SETTINGS)    //this setting class open the setting of the phone(provide Path)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity) //this code will code the app
            }
            dialog.create()
            dialog.show()
        }


    }

    // below is subclass which inherits AsyncTask class // AT ckass needs params, progress, result
    // performing database operation we need context
    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
/*
  1-> check the DB if the book is favourites or not
  2-> save the book into DB as favourites
  3-> Remove the  favourites books
* */

        //initialization of DB class globally
        // we use databasebuilder method of class Room, It takes three parameters
        // For building database use build method
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean

         {

            when (mode) {
                1 -> {
                    //check the DB if the book is favourites or not
                    val book: BookEntity? = db.bookDao().getBookId(bookEntity.book_id.toString())
                    db.close()  // mandatory to close databse after doing some operation otherwise it will takes some unnecessary data
                    return book != null  // if function return null it will return false
                }
                2 -> {
                    //save the book into DB as favourites
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the  favourites books
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }

            return false
        }
    }
}