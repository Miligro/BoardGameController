package com.example.bgc

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GamesListAdapter: RecyclerView.Adapter<GamesListAdapter.MyViewHolder>() {

    private var gamesList = emptyList<GameAddOn>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val lpText: TextView = itemView.findViewById(R.id.lpText)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val releaseYearText: TextView = itemView.findViewById(R.id.releaseText)
        val rankingText: TextView = itemView.findViewById(R.id.rankingText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = gamesList[position]
        val lp = position + 1
        holder.lpText.text = lp.toString()
        holder.titleText.text = currentItem.title
        holder.releaseYearText.text = currentItem.releaseYear.toString()
        holder.rankingText.text = currentItem.ranking.toString()
        Picasso.get().load(currentItem.img).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(games: List<GameAddOn>){
        this.gamesList = games
        notifyDataSetChanged()
    }
}