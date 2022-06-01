package com.example.bgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bgc.databinding.FragmentGamesBinding

class Games : Fragment() {
    private var _binding: FragmentGamesBinding? = null
    private lateinit var dbHandler: MyDBHandler
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_games, container, false)

        val adapter = GamesListAdapter()
        adapter.setData(games)

        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val gameListArray: ArrayList<GameAddOn> = dbHandler.getGames()
        val sizeGames = gameListArray.size
        Toast.makeText(activity, "Ilość gier: $sizeGames", Toast.LENGTH_LONG).show()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}