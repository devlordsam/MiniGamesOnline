package com.lordsam.minigamesonline

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.lordsam.minigamesonline.databinding.ActivityLoginBinding
import com.lordsam.minigamesonline.firebase.MyFirebase
import com.lordsam.minigamesonline.utils.Constants.RC_SIGN_IN

class LoginActivity : MainActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAnalytics: FirebaseAnalytics

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAnalytics = FirebaseAnalytics.getInstance(this)


        binding.llGoogle.setOnClickListener {
            MyFirebase(this, this).signIn()
        }

        binding.btnLoginUsername.setOnClickListener {
            if (binding.etLoginUsername.text.toString().isEmpty())
                showErrorSnackBar("Please, enter username!", true)
            else if (!binding.etLoginUsername.text.toString().matches("^[a-zA-Z0-9]*$".toRegex()))
                showErrorSnackBar("Invalid characters in username, use Alphanumeric only!", true)
            else {
                showProgressDialog("Please wait...")
                MyFirebase(this, this).createUsername(
                    binding.etLoginUsername.text.toString().trim()
                )
            }
            //close keypad after submit wrt CL
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.clLogin.windowToken, 0)
        }

        MyFirebase(this, this).isUserSignedIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                MyFirebase(this, this).firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    fun signInSuccess() {
        binding.llGoogle.visibility = View.GONE
        binding.llLoginUserName.visibility = View.VISIBLE
    }

    fun createUserSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, GamesActivity::class.java))
    }

}