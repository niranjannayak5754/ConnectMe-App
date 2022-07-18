package com.example.connectme.utils.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connectme.R
import com.example.connectme.utils.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class CartListsAdapter(private val context: Context,private val listener: CartItemClicked) : RecyclerView.Adapter<CartListsViewHolder>() {

    private var totalPrice = 0
    private val myCartItems = ArrayList<CartItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.each_cart_item, parent, false)

        val viewHolder = CartListsViewHolder(view)
        viewHolder.cartItemDelete.setOnClickListener{
            listener.onDeleteButtonClicked(myCartItems[viewHolder.adapterPosition])
        }
        viewHolder.cartItemBuy.setOnClickListener{
            listener.onBuyNowButtonClicked(myCartItems[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CartListsViewHolder, position: Int) {
        val currentCartItem = myCartItems[position]
        holder.cartItemName.text = currentCartItem.petName
        holder.cartItemBreedName.text = currentCartItem.breedName
        holder.cartItemQuantity.text = currentCartItem.quantity
        Glide.with(holder.itemView.context).load(currentCartItem.imageUrl).into(holder.cartItemImage)
        val cartItemPrice= (currentCartItem.price!!.toInt() * currentCartItem.quantity.toInt())
        "Rs. $cartItemPrice".also { holder.cartItemPrice.text = it }

        totalPrice += cartItemPrice // overall price of all the cart items
        val intent = Intent("MyTotalAmount")
        intent.putExtra("totalAmount", totalPrice)

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun getItemCount(): Int {
        return myCartItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCartItem(updatedCartItems: ArrayList<CartItem>) {
        myCartItems.clear()
        myCartItems.addAll(updatedCartItems)

        notifyDataSetChanged()
    }

}

class CartListsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cartItemImage: ImageView = itemView.findViewById(R.id.ivEachCartItem)
    val cartItemName: TextView = itemView.findViewById(R.id.tvEachCartItemName)
    val cartItemBreedName: TextView = itemView.findViewById(R.id.tvEachCartItemBreedName)
    val cartItemPrice: TextView = itemView.findViewById(R.id.eachCartItemPriceTV)
    val cartItemQuantity: TextView = itemView.findViewById(R.id.quantitySpinner)
    val cartItemDelete : ImageView = itemView.findViewById(R.id.eachCartItemDeleteBtn)
    val cartItemBuy : Button = itemView.findViewById(R.id.eachCartItemBuyBtn)

}

interface CartItemClicked{
    fun onDeleteButtonClicked(item: CartItem)
    fun onBuyNowButtonClicked(item: CartItem)
}