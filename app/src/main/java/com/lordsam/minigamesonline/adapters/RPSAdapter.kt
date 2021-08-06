package com.lordsam.minigamesonline.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lordsam.minigamesonline.R
import com.lordsam.minigamesonline.RPSActivity
import com.lordsam.minigamesonline.utils.Games
import com.lordsam.minigamesonline.utils.Player

class RPSAdapter (
    private val context: Context,
    private val list :ArrayList<Player>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.card_rv_player,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            holder.itemView.findViewById<TextView>(R.id.tvCardGameName).text = model.username

            holder.itemView.findViewById<Button>(R.id.btnPlayerSend).setOnClickListener {
                //Send game request
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}