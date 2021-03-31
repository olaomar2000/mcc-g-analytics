package com.example.googleanalytics.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalytics.Modle.Category
import com.example.googleanalytics.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categories_item.view.*


class CategoriesAdapter(var activity: Context?, var data: MutableList<Category>, var clickListener: onCategoryItemClickListener): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCategory  =itemView.Category_Image

        val nameCategory=itemView.Category_Name

        fun initialize(data: Category, action:onCategoryItemClickListener){
           Picasso.get().load(data.imageCategory).into(imageCategory)

            nameCategory.text = data.nameCategory

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.categories_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.initialize(data.get(position), clickListener)
    }
    interface onCategoryItemClickListener{
        fun onItemClick(data:Category, position: Int)
    }
}