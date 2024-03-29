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
        super.onViewCreated(view, savedInstanceState)
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        val addOnsListArray: ArrayList<GameAddOn> = dbHandler.getAddOns()
        adapter.setData(addOnsListArray)

        binding.titleSort.setOnClickListener {
            if (binding.titleSort.text == getString(R.string.title_desc)){
                addOnsListArray.sortBy{it.title}
                adapter.setData(addOnsListArray)
                binding.titleSort.text = getString(R.string.title_asc)
            }else if(binding.titleSort.text == getString(R.string.title_asc) || binding.titleSort.text == getString(R.string.title)){
                addOnsListArray.sortByDescending{it.title}
                adapter.setData(addOnsListArray)
                binding.titleSort.text = getString(R.string.title_desc)
            }
            binding.releaseSort.text = getString(R.string.release)
        }
        binding.releaseSort.setOnClickListener {
            if (binding.releaseSort.text == getString(R.string.release_desc)) {
                addOnsListArray.sortBy{it.releaseYear}
                adapter.setData(addOnsListArray)
                binding.releaseSort.text = getString(R.string.release_asc)
            } else if (binding.releaseSort.text == getString(R.string.release_asc) || binding.releaseSort.text == getString(
                    R.string.release
                )
            ) {
                addOnsListArray.sortByDescending{it.releaseYear}
                adapter.setData(addOnsListArray)
                binding.releaseSort.text = getString(R.string.release_desc)
            }
            binding.titleSort.text = getString(R.string.title)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}