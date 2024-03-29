package com.example.bgc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.lifecycle.ViewModelProvider
import com.example.bgc.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

class User(username: String, numberOfGames: Int, numberOfAddOns: Int, lastSync: String) {
    var username: String? = username
    var numberOfGames: Int? = numberOfGames
    var numberOfAddOns: Int? = numberOfAddOns
    var lastSync: String? = lastSync
}

class GameRankingHistory(var id: Long, var syncDate: String, var ranking: Int) {}

class GameAddOn{
    var id: Long = 0
    var title: String = ""
    var img: String = ""
    var releaseYear: Int = 0
    var ranking: Int? = null
    var type: String = ""

    constructor(id: Long, title: String, img: String, releaseYear: Int, ranking: Int){
        this.id = id
        this.title = title
        this.img = img
        this.releaseYear = releaseYear
        this.ranking = ranking
        this.type = "Game"
    }

    constructor(id: Long, title: String, img: String, releaseYear: Int){
        this.id = id
        this.title = title
        this.img = img
        this.releaseYear = releaseYear
        this.type = "Expansion"
    }
}