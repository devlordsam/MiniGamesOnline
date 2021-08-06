package com.lordsam.minigamesonline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.lordsam.minigamesonline.databinding.ActivityGamesBinding
import com.lordsam.minigamesonline.databinding.ActivityRpsactivityBinding
import com.lordsam.minigamesonline.firebase.MyFirebase

class RPSActivity : MainActivity() {

    private lateinit var binding: ActivityRpsactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRpsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddPlayer.setOnClickListener {
            if (binding.etAddPlayer.text.toString().isEmpty()){
                showErrorSnackBar("Please, enter username!", true)
            }else if (!binding.etAddPlayer.text.toString().matches("^[a-zA-Z0-9]*$".toRegex())){
                showErrorSnackBar("Please, enter valid username!", true)
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                MyFirebase(this, this).getPlayer(binding.etAddPlayer.text.toString().trim())
            }
        }
    }

    fun friendAdded() {
        hideProgressDialog()
        showErrorSnackBar("Friend Added!", false)
    }

    fun getFriends(){
        //TODO: getFriends()
    }
}