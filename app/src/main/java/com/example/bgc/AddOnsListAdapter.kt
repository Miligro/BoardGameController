package com.example.bgc

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AddOnsListAdapter: RecyclerView.Adapter<AddOnsListAdapter.MyViewHolder>() {

    private var addOnsList = emptyList<GameAddOn>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val lpText: TextView = itemView.findViewById(R.id.lpText)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val releasText: TextView = itemView.findViewById(R.id.releaseText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row_add_on, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = addOnsList[position]
        val lp = (position + 1).toString() + "."
        holder.lpText.text = lp
        holder.titleText.text = currentItem.title
        holder.releasText.text = currentItem.releaseYear.toString()
        Picasso.get().load(currentItem.img).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return addOnsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(addOns: List<GameAddOn>){
        this.addOnsList = addOns
        notifyDataSetChanged()
    }
}