package com.example.bgc

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        adapter = GamesListAdapter(findNavController())

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val gameListArray: ArrayList<GameAddOn> = dbHandler.getGames()
        adapter.setData(gameListArray)

        binding.titleSort.setOnClickListener {
            if (binding.titleSort.text == getString(R.string.title_desc)){
                gameListArray.sortBy { it.title }
                adapter.setData(gameListArray)
                binding.titleSort.text = getString(R.string.title_asc)
            }else if(binding.titleSort.text == getString(R.string.title_asc) || binding.titleSort.text == getString(R.string.title)){
                gameListArray.sortByDescending { it.title }
                adapter.setData(gameListArray)
                binding.titleSort.text = getString(R.string.title_desc)
            }
            binding.releaseSort.text = getString(R.string.release)
            binding.rankingSort.text = getString(R.string.ranking)
        }
        binding.releaseSort.setOnClickListener {
            if (binding.releaseSort.text == getString(R.string.release_desc)){
                gameListArray.sortBy { it.releaseYear }
                adapter.setData(gameListArray)
                binding.releaseSort.text = getString(R.string.release_asc)
            }else if(binding.releaseSort.text == getString(R.string.release_asc) || binding.releaseSort.text == getString(R.string.release)){
                gameListArray.sortByDescending { it.releaseYear }
                adapter.setData(gameListArray)
                binding.releaseSort.text = getString(R.string.release_desc)
            }
            binding.titleSort.text = getString(R.string.title)
            binding.rankingSort.text = getString(R.string.ranking)
        }
        binding.rankingSort.setOnClickListener {
            if (binding.rankingSort.text == getString(R.string.ranking_desc)){
                gameListArray.sortBy { it.ranking }
                adapter.setData(gameListArray)
                binding.rankingSort.text = getString(R.string.ranking_asc)
            }else if(binding.rankingSort.text == getString(R.string.ranking_asc) || binding.rankingSort.text == getString(R.string.ranking)){
                gameListArray.sortByDescending { it.ranking }
                adapter.setData(gameListArray)
                binding.rankingSort.text = getString(R.string.ranking_desc)
            }
            binding.titleSort.text = getString(R.string.title)
            binding.releaseSort.text = getString(R.string.release)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}