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
import com.example.connectme.utils.model.Pet

class PetDetailsAdapter( private val listener : PetDetailsItemClicked) : RecyclerView.Adapter<PetDetailsViewHolder>() {
    private val items = ArrayList<Pet>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_pet_item,parent,false)
        val viewHolder = PetDetailsViewHolder(view)
        view.setOnClickListener { listener.onItemClicked(items[viewHolder.adapterPosition])}
        return  viewHolder
    }

    override fun onBindViewHolder(holder: PetDetailsViewHolder, position: Int) {
        val currentItem = items[position]
        currentItem.petName.also { holder.petName.text=it }
        currentItem.breedName.also { holder.breedName.text=it }
        currentItem.price.also { holder.price.text= "$it$" }
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updatePets(updatedPets:ArrayList<Pet>){
        items.clear()
        items.addAll(updatedPets)
//        to notify that the data need to be updated to reRun getItemCount, onCreate and onBindView functions
        notifyDataSetChanged()
    }
}

class PetDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val petName : TextView = itemView.findViewById(R.id.tvPetName)
    val image: ImageView = itemView.findViewById(R.id.ivPetItem)
    val breedName : TextView = itemView.findViewById(R.id.tvPetBreedName)
    val price : TextView = itemView.findViewById(R.id.tvPetItemPrice)
}
interface PetDetailsItemClicked{
    fun onItemClicked(item: Pet)
}