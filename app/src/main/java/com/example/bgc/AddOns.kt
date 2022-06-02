package com.example.bgc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bgc.databinding.FragmentAddOnsBinding


class AddOns : Fragment() {
    private var _binding: FragmentAddOnsBinding? = null
    private lateinit var dbHandler: MyDBHandler
    private val binding get() = _binding!!

    private lateinit var adapter: AddOnsListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOnsBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView;
        adapter = AddOnsListAdapter()

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val addOnsListArray: ArrayList<GameAddOn> = dbHandler.getAddOns()
        adapter.setData(addOnsListArray)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}