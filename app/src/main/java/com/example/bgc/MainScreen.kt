package com.example.bgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentMainScreenBinding


class MainScreen : Fragment() {


    private var _binding: FragmentMainScreenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gamesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_gamesAddOns)
        }
        binding.AddOnsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_gamesAddOns)
        }
        binding.syncBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_synchronization)
        }
        binding.clearDataBtn.setOnClickListener {
            Toast.makeText(activity, "Czyszczenie danych", Toast.LENGTH_LONG).show()
//            findNavController().navigate(R.id.action_mainScreen_to_gamesAddOns)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}