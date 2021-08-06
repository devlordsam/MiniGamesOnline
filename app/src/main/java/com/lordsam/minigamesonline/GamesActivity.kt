package com.lordsam.minigamesonline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lordsam.minigamesonline.adapters.GamesAdapter
import com.lordsam.minigamesonline.databinding.ActivityGamesBinding
import com.lordsam.minigamesonline.utils.Games

class GamesActivity : AppCompatActivity() {

    private lateinit var binding :ActivityGamesBinding
    private lateinit var arrGames :ArrayList<Games>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrGames = ArrayList()
        arrGames.add(Games("Rock, Paper, Scissor", R.drawable.rps))

        setUpGameList(arrGames)
    }

    private fun setUpGameList(arrGames: java.util.ArrayList<Games>) {
        binding.rvGames.layoutManager = LinearLayoutManager(this)
        binding.rvGames.setHasFixedSize(true)

        val adapter = GamesAdapter(this,arrGames)
        binding.rvGames.adapter = adapter
    }

}