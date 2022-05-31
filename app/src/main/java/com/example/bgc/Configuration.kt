package com.example.bgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentConfigurationBinding

class Configuration : Fragment() {
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
        dbHandler.deleteAllGamesAddOns()
        binding.progressBar.progress = 0
        binding.saveUserBtn.setOnClickListener{
            binding.saveUserBtn.isEnabled = false
            username = binding.usernameText.text.toString()
            val user: User = User(username, 0, 0, "123")
            dbHandler.addUser(user)
            downloadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun downloadData() {
        val cd = UserStartDownloader(binding.progressBar, requireContext(), username, findNavController())
        cd.execute()
    }
}