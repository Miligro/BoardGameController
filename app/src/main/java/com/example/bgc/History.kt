package com.example.bgc

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bgc.databinding.FragmentHistoryBinding


class History : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var dbHandler: MyDBHandler
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryRankingAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView;
        adapter = HistoryRankingAdapter()

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val historyList: ArrayList<GameRankingHistory> = dbHandler.getRankingHistory(5867) // Dodać ID
        adapter.setData(historyList)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}