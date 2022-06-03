package com.example.bgc

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentSynchronizationBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class Synchronization : Fragment() {

    private var _binding: FragmentSynchronizationBinding? = null
    private lateinit var dbHandler: MyDBHandler
    private val binding get() = _binding!!
    private var user: User? = null
    private lateinit var syncDate: LocalDateTime
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSynchronizationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        user = dbHandler.findUser()

        syncDate = LocalDateTime.parse(user?.lastSync)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        binding.lastSyncText.text = syncDate.format(formatter)

        binding.synchronizeBtn.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val hours: Long = Duration.between(syncDate, currentDate).toHours()
            if(hours < 24){
                confirmSync()
            }else{
                syncData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmSync(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz dokonać synchronizacji?")
        builder.setPositiveButton("Zatwierdz",
            DialogInterface.OnClickListener { dialog, which ->
                syncData()
            })
        builder.setNegativeButton("Anuluj",null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun syncData(){
        val layouts = ArrayList<LinearLayout>()
        val progressBars = ArrayList<ProgressBar>()
        layouts.add(binding.downloadLayout)
        layouts.add(binding.loadGamesLayout)
        layouts.add(binding.loadAddOnsLayout)

        progressBars.add(binding.downloadProgress)
        progressBars.add(binding.laodGamesProgress)
        progressBars.add(binding.laodAddOnsProgress)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.synchronizeBtn.isEnabled = false
        val cd = UserStartDownloader(activity as? AppCompatActivity,binding.synchronizeBtn, layouts, progressBars, requireContext(), user?.username, findNavController(), "synchronization")
        cd.execute()
    }
}