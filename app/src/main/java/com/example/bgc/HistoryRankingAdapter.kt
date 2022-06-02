package com.example.bgc

import android.annotation.SuppressLint
import android.text.method.Touch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryRankingAdapter: RecyclerView.Adapter<HistoryRankingAdapter.MyViewHolder>() {

    private var historyList = emptyList<GameRankingHistory>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val lpText: TextView = itemView.findViewById(R.id.lpText)
        val syncDate: TextView = itemView.findViewById(R.id.syncDateText)
        val ranking: TextView = itemView.findViewById(R.id.rankingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row_history, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = historyList[position]
        val lp = (position + 1).toString() + "."
        holder.lpText.text = lp
        val syncDateD = LocalDateTime.parse(currentItem.syncDate)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        holder.syncDate.text = syncDateD.format(formatter)
        holder.ranking.text = currentItem.ranking.toString()
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(gameRankings: List<GameRankingHistory>){
        this.historyList = gameRankings
        notifyDataSetChanged()
    }
}