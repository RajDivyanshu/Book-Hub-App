package com.KotlinNinja.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.KotlinNinja.bookhub.R
import com.KotlinNinja.bookhub.activity.DescriptionActivity
import com.KotlinNinja.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }//A ViewHolder is responsible for creating the Views for each row item of the RecyclerView.
//A ViewHolder is responsible for creating the Views for each row item of the RecyclerView.

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        //    holder.imgBookImage.setImageResource(book.bookImage)  //image is put by this mathod
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover)
            .into(holder.imgBookImage)

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
        //Picasso may not load correctly use (error() ) for default images
        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
        //Picasso may not load correctly use (error() ) for default images


        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)  // context - fragment activity
            intent.putExtra("book_id",book.bookId)   //putting the bookId int the intent
            context.startActivity(intent)       //this startActivity is used when we open an activity from current activity
                                                //we use context here to use startActivity method
        }

//        holder.llContent.setOnClickListener {
//            Toast.makeText(context, "Clicked On ${holder.txtBookName.text}", Toast.LENGTH_SHORT)
//                .show()
//
//        }

    }       //setBackgroundResource() is also used for background image


    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice: TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgBookImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    //if total 15 items then - Since the screen can only display 6 items, the RecyclerView creates 6 new ViewHolder while the others
    // are recycled using the onBindViewHolder() method. Therefore, out of total 15, 9 are recycled.
}