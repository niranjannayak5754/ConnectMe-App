package com.example.connectme.utils.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connectme.R
import com.example.connectme.utils.model.CategoryData
import com.example.connectme.utils.model.Pet

class PetMenuAdapter( private val listener : PetsItemClicked) : RecyclerView.Adapter<PetsViewHolder>() {
    private val items = ArrayList<CategoryData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_pet_menu_item,parent,false)
        val viewHolder = PetsViewHolder(view)
        view.setOnClickListener { listener.onItemClicked(items[viewHolder.adapterPosition].petName.toString())}
        return  viewHolder
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        val currentItem = items[position]
        currentItem.petName.also { holder.menuItem.text=it }
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(updatedNews:ArrayList<CategoryData>){
        items.clear()
        items.addAll(updatedNews)
//        to notify that the data need to be updated to reRun getItemCount, onCreate and onBindView functions
        notifyDataSetChanged()
    }
}

class PetsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val menuItem : TextView = itemView.findViewById(R.id.tvPetMenuItemName)
    val image: ImageView = itemView.findViewById(R.id.ivPetMenuItem)
}
interface PetsItemClicked{
    fun onItemClicked(category: String)
}