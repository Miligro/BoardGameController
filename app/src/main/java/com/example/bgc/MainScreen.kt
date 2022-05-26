package com.example.bgc

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
            clearDataBase()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearDataBase(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz wyczyścić dane?")
        builder.setPositiveButton("Zatwierdz",
            DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(activity, "Zatwierdzono", Toast.LENGTH_SHORT).show()})
        builder.setNegativeButton("Anuluj",
            DialogInterface.OnClickListener { dialog, which -> })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}