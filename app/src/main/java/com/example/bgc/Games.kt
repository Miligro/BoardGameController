package com.example.bgc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bgc.databinding.FragmentGamesBinding


class Games : Fragment() {
    private var _binding: FragmentGamesBinding? = null
    private lateinit var dbHandler: MyDBHandler
    private val binding get() = _binding!!

    private lateinit var adapter: GamesListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView;
        adapter = GamesListAdapter()

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val gameListArray: ArrayList<GameAddOn> = dbHandler.getGames()
        adapter.setData(gameListArray)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}