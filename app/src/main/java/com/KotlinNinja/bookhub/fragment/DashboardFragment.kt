package com.KotlinNinja.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.KotlinNinja.bookhub.R
import com.KotlinNinja.bookhub.adapter.DashboardRecyclerAdapter
import com.KotlinNinja.bookhub.model.Book
import com.KotlinNinja.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val bookInfoList = arrayListOf<Book>()
    var ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            //sort according to the name if rating is same
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)//used to adding menu to a fragment but in activity automatically done

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE   //visible when fragment is loaded

        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)
       val url = "http://13.235.250.119/v1/book/fetch_books/"
      //  val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    //here we handle responses
                    println("Response is $it")
                    try {   //for resolving Json Exception error
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                                recycleAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recycleAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error has occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {

                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }   //here we handle error
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "00d5a14c31a944"       //00d5a14c31a944
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog =
                AlertDialog.Builder(activity as Context)   // AlertDialog(class)  Builder(subclass)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent =
                    Intent(Settings.ACTION_WIRELESS_SETTINGS)    //this setting class open the setting of the phone(provide Path)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity) //this code will code the app
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) { //this method is used to add menu items to the toolbar
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)    //sort the list in increasing order
            bookInfoList.reverse()
        }
        recycleAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}