package com.example.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bookhub.R
import com.example.bookhub.activity.DescriptionActivity
import com.example.bookhub.model.Book
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_dashboard_single_row.view.*

class DashboardRecyclerAdapter(val context:Context,val itemList:ArrayList<Book>) :RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DashboardViewHolder {
        val view=LayoutInflater.from(p0.context).inflate(R.layout.recycler_dashboard_single_row,p0,false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(p0: DashboardViewHolder, p1: Int) {
        val book=itemList[p1]
        p0.name.text=book.bookName
        p0.author.text=book.bookAuthor
        p0.price.text=book.bookPrice
        p0.rating.text=book.bookRating
        //p0.image.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(p0.image)

        p0.llcontent.setOnClickListener {
            val intent= Intent(context ,DescriptionActivity::class.java)
            intent.putExtra("bookId",book.bookId)
            context.startActivity(intent)
        }



    }

    class DashboardViewHolder(view: View):RecyclerView.ViewHolder(view){
        val name:TextView=view.findViewById(R.id.txtBookName)
        val author:TextView=view.findViewById(R.id.txtBookAuthor)
        val price:TextView=view.findViewById(R.id.txtBookPrice)
        val rating:TextView=view.findViewById(R.id.txtBookRating)
        val image:ImageView=view.findViewById(R.id.imgBookImage)
        val llcontent:LinearLayout=view.findViewById(R.id.llcontent)
    }

}