package com.example.bgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.bgc.databinding.FragmentConfigurationBinding

class Configuration : Fragment() {
    private var _binding: FragmentConfigurationBinding? = null
    private lateinit var dbHandler: MyDBHandler

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHandler = MyDBHandler(requireActivity(), null, null, 1)

        binding.saveUserBtn.setOnClickListener{
            val username: String = binding.usernameText.text.toString()
            val user: User = User(username, 0, 0, "123")
            dbHandler.addUser(user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}