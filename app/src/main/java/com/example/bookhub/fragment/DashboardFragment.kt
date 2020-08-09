package com.example.bookhub.fragment


import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.adapter.DashboardRecyclerAdapter
import com.example.bookhub.model.Book
import com.example.bookhub.util.ConnectionManager
import org.json.JSONException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class DashboardFragment : Fragment() {


    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btnCheckConnetivity: Button

    val booklist = arrayListOf(
        "Anna Karenina", "Madame Bovary", "War and Peace", "The Great Gatsby", "Lolita", "Middlemarch",
        "The Adventures of Huckleberry Finn", " The Stories of Anton Chekhov", "In Search of Lost Time", " Hamlet"
    )
    lateinit var recycleAdapter: DashboardRecyclerAdapter

    val bookInfoList = arrayListOf<Book>()
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        progressLayout=view.findViewById(R.id.progessLayout)
        progressBar=view.findViewById(R.id.progessBar)
        progressBar.visibility=View.VISIBLE

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)



        btnCheckConnetivity = view.findViewById(R.id.btnCheckConnectivity)

        btnCheckConnetivity.setOnClickListener {

            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection found")
                dialog.setPositiveButton("Ok") { text, Listener -> }
                dialog.setNegativeButton("Cancel") { text, Listener -> }

                dialog.create()
                dialog.show()
            }

            else
            { val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not found")
                dialog.setPositiveButton("Ok") { text, Listener -> }
                dialog.setNegativeButton("Cancel") { text, Listener -> }

                dialog.create()
                dialog.show()
            }


        }

        val queue=Volley.newRequestQueue(activity as Context)

        val url="http://13.235.250.119/v1/book/fetch_books"

        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest= object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {

                //Here we handle the response
                try{

                    progressBar.visibility=View.GONE
                    val success=it.getBoolean("success")

                    if(success)
                    {
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length())
                        {
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject=Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")


                            )
                            bookInfoList.add(bookObject)

                            recycleAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)

                            recyclerDashboard.adapter = recycleAdapter

                            recyclerDashboard.layoutManager = layoutManager

                            // adding lines between different list items
                            /*recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context, (layoutManager as LinearLayoutManager).orientation
                                )
                            )*/

                        }

                    }
                    else
                    {
                        Toast.makeText(activity as Context,"Some error occured",Toast.LENGTH_SHORT).show()
                    }
                }
                catch(e:JSONException)
                {
                    Toast.makeText(activity as Context,"Some unexpected error occured", Toast.LENGTH_SHORT).show()
                }



                println("Response is $it ")
            },Response.ErrorListener {

                //   Here we handle errors
                Toast.makeText(activity as Context,"Volley unexpected error occured", Toast.LENGTH_SHORT).show()
            }
            )
            {
                override fun getHeaders(): MutableMap<String, String> {

                    val headers=HashMap<String,String>()
                    headers["Content Type"]="application/json"
                    headers["token"]="fb6406b0922a19"
                    return headers
                }



            }

            queue.add(jsonObjectRequest)

        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open settings") { text, Listener ->
                val settings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settings)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, Listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }



            return view
        }


    }
