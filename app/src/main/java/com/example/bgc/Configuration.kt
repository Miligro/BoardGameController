package com.example.bgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentConfigurationBinding

class Configuration : Fragment(), LifecycleOwner {
    private var _binding: FragmentConfigurationBinding? = null
    private lateinit var dbHandler: MyDBHandler

    private var username: String = ""
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        binding.saveUserBtn.setOnClickListener{
            binding.saveUserBtn.isEnabled = false
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            username = binding.usernameText.text.toString()
            val user = User(username, 0, 0, "")
            dbHandler.addUser(user)
            downloadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun downloadData() {
        val layouts = ArrayList<LinearLayout>()
        val progressBars = ArrayList<ProgressBar>()
        layouts.add(binding.downloadLayout)
        layouts.add(binding.loadGamesLayout)
        layouts.add(binding.loadAddOnsLayout)

        progressBars.add(binding.laodGamesProgress)
        progressBars.add(binding.laodAddOnsProgress)
        val cd = UserStartDownloader(binding.downloadProgressText, activity as? AppCompatActivity,binding.saveUserBtn, layouts, progressBars, requireContext(), username, findNavController(), "configuration")
        cd.execute()
    }
}