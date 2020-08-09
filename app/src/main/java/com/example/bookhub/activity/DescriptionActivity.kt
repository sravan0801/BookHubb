package com.example.bookhub.activity

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.database.BookDatabase
import com.example.bookhub.database.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject



class DescriptionActivity : AppCompatActivity() {


    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var aboutBook: TextView
    lateinit var btnaddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout

    var book_id: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookPrice)
        imgBookImage = findViewById(R.id.imgBookImage)
        aboutBook = findViewById(R.id.aboutBook)
        btnaddToFav = findViewById(R.id.btnAddToFav)
        progressLayout = findViewById(R.id.progessLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progessBar)
        progressBar.visibility = View.VISIBLE

        if (intent != null) {
            book_id = intent.getStringExtra("bookId")
        } else {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some unexpected error occured.", Toast.LENGTH_SHORT).show()
        }

        if (book_id == "100") {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some unexpected error occured.", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("bookId", book_id)
        print(book_id)

        val jsonRequest = object : JsonObjectRequest(Request.Method.PUT,url,jsonParams,Response.Listener {

            try {
                val success=it.getBoolean("success")
                if(success)
                {
                    val bookJsonObject=it.getJSONObject("book_id")
                    progressLayout.visibility=View.GONE
                    progressBar.visibility=View.GONE

                    val bookImageUrl=bookJsonObject.getString("image")
                    Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                    txtBookName.text=bookJsonObject.getString("name")
                    txtBookAuthor.text=bookJsonObject.getString("author")
                    txtBookRating.text=bookJsonObject.getString("rating")
                    txtBookPrice.text=bookJsonObject.getString("price")
                    aboutBook.text=bookJsonObject.getString("description")

                    val bookEntity=BookEntity(
                    book_id?.toInt() as Int,
                    txtBookName.text.toString(),
                    txtBookAuthor.text.toString(),
                    txtBookRating.text.toString(),
                    txtBookPrice.text.toString(),
                    aboutBook.text.toString(),
                    bookImageUrl
                    )

                    val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                    val isFav=checkFav.get()
                    if(isFav)
                    {
                        btnaddToFav.text="Remove From Fav"
                        val favColour=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                        btnaddToFav.setBackgroundColor(favColour)

                    }
                    else
                    {
                        btnaddToFav.text="Add to Fav"
                        val favColour1=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                        btnaddToFav.setBackgroundColor(favColour1)
                    }
                btnaddToFav.setOnClickListener {
                    if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get())
                    {
                        val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                        val result=async.get()
                    }
                }
                }
                else
                {
                    Toast.makeText(this@DescriptionActivity, "Some unexpected error occured.", Toast.LENGTH_SHORT).show()
                }

            }

            catch (e:JSONException)
            {
                Toast.makeText(this@DescriptionActivity, "Some unexpected error occured.", Toast.LENGTH_SHORT).show()
            }

        },Response.ErrorListener {
            Toast.makeText(this@DescriptionActivity, "Volley error $it.", Toast.LENGTH_SHORT).show()

        })
        {
            override fun getHeaders(): MutableMap<String, String> {

                val headers=HashMap<String,String>()
                headers["Content Type"]="application/json"
                headers["token"]="fb6406b0922a19"
                return headers
            }
        }
        queue.add(jsonRequest)
    }


    class DBAsyncTask(val context:Context,val bookEntity: BookEntity,val mode:Int):AsyncTask<Void,Void,Boolean>()
    {
        val db= Room .databaseBuilder(context,BookDatabase::class.java,"book-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode)
            {
                1->{// Check DB if book is fav or not
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id)
                    db.close()
                    return book!=null
                    }
                2->{// Save book in DB as Fav
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                     }
                3->{
                    //Remove the fav book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
}