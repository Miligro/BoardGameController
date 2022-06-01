package com.example.bgc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GamesListAdapter: RecyclerView.Adapter<GamesListAdapter.MyViewHolder>() {

    private var gamesList = emptyList<GameAddOn>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = gamesList[position]

    }

    override fun getItemCount(): Int {
        return gamesList.size

    }

    fun setData(games: List<GameAddOn>){
        this.gamesList = games
        notifyDataSetChanged()
    }
}