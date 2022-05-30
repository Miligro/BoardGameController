package com.example.bgc

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentMainScreenBinding
import kotlin.system.exitProcess


class MainScreen : Fragment() {

    private lateinit var dbHandler: MyDBHandler
    private var user: User? = null

    private var _binding: FragmentMainScreenBinding? = null

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
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        user = dbHandler.findUser()
        if(user == null){
            findNavController().navigate(R.id.action_mainScreen_to_configuration)
        } else{
            setUserData()
        }
        binding.gamesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_gamesAddOns)
        }
        binding.AddOnsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_configuration)
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

    private fun setUserData(){
        binding.usernameTextView.text = user?.username
        binding.numOfGamesTextView.text = user?.numberOfGames.toString()
        binding.numOfAddOnsTextView.text = user?.numberOfAddOns.toString()
        binding.lastSyncTextView.text = user?.lastSync
    }

    private fun clearDataBase(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz wyczyścić dane?")
        builder.setPositiveButton("Zatwierdz",
            DialogInterface.OnClickListener { dialog, which ->
                dbHandler.deleteAllUsers()
                activity?.finish()
                exitProcess(0)
            })
        builder.setNegativeButton("Anuluj",
            DialogInterface.OnClickListener { dialog, which -> })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}