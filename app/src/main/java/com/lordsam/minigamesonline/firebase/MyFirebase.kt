package com.lordsam.minigamesonline.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.lordsam.minigamesonline.*
import com.lordsam.minigamesonline.utils.Constants
import com.lordsam.minigamesonline.utils.Users

class MyFirebase(private val context: Context, private val activity: Activity) {

    private var auth: FirebaseAuth = Firebase.auth
    private var googleSignInClient: GoogleSignInClient? = null
    private val mFireStore = FirebaseFirestore.getInstance()


    fun signIn() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient!!.signInIntent
        activity.startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                    //val user = auth.currentUser
                    if (activity is LoginActivity) {
                        activity.signInSuccess()
                    }
                } else {
                    Toast.makeText(context, "Sign in failled!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createUsername(username: String) {

        mFireStore.collection(Constants.USERNAMES)
            .document(getUserID())
            .get()
            .addOnSuccessListener {
                if (it.get("username") == username) {
                    if (activity is LoginActivity) {
                        activity.hideProgressDialog()
                        activity.showErrorSnackBar("Username exists", true)
                    }
                } else {
                    createNewUsername(username)
                }
            }.addOnFailureListener {
                if (activity is LoginActivity) {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(it.message.toString(), true)
                }
            }
    }

    private fun createNewUsername(username: String) {

        val user = Users(username, getUserID(), ArrayList())

        mFireStore.collection(Constants.USERNAMES)
            .document(getUserID())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                if (activity is LoginActivity) {
                    activity.createUserSuccess()
                }
            }.addOnFailureListener {
                if (activity is LoginActivity) {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(it.message.toString(), true)
                }
            }
    }

    fun isUserSignedIn() {
        if (auth.currentUser != null) {
            if (activity is LoginActivity) {
                activity.signInSuccess()
            }
        }

    }

    fun getUserID(): String {
        return if (auth.currentUser != null) auth.currentUser!!.uid else ""
    }

    fun ifUserExists() {
        if (auth.currentUser != null) {
            mFireStore.collection(Constants.USERNAMES)
                .document(getUserID())
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        if (activity is SplashActivity) {
                            context.startActivity(Intent(context, GamesActivity::class.java))
                        }
                    } else {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                }.addOnFailureListener {
                    if (activity is SplashActivity) {
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    fun getPlayer(username: String) {

        //getting uid
        mFireStore.collection(Constants.USERNAMES)
            .whereEqualTo(Constants.USERNAME, username)
            .get()
            .addOnSuccessListener {it2 ->

                var uid = ""

                try {
                    for (document in it2.documents) {
                        uid = document.toObject(Users::class.java)!!.uid
                    }
                }catch (e :Exception){
                    if (activity is RPSActivity){
                        activity.hideProgressDialog()
                        activity.showErrorSnackBar("User not found", true)
                    }
                }

                //check if user exists
                if (uid != "") {
                    mFireStore.collection(Constants.USERNAMES)
                        .document(uid)
                        .get()
                        .addOnSuccessListener {
                            if (it.get("username") == username) {
                                addFriend(it.toObject(Users::class.java))
                            } else {
                                if (activity is RPSActivity) {
                                    activity.hideProgressDialog()
                                    activity.showErrorSnackBar("User does not exists", true)
                                }
                            }
                        }.addOnFailureListener {
                            if (activity is RPSActivity) {
                                activity.hideProgressDialog()
                                activity.showErrorSnackBar(it.message.toString(), true)
                            }
                        }
                }else{
                    if (activity is RPSActivity) {
                        activity.hideProgressDialog()
                        activity.showErrorSnackBar("User not found", true)
                    }
                }
                //

            }.addOnFailureListener {
                if (activity is RPSActivity) {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(it.message.toString(), true)
                }
            }
    }

    private fun addFriend(user: Users?) {

        mFireStore.collection(Constants.USERNAMES)
            .document(getUserID())
            .get()
            .addOnSuccessListener {
                val me = it.toObject(Users::class.java)
                for (i in me?.friend!!){
                    if ((i.uid != user!!.uid) && (user.uid != getUserID())){
                        me.friend.add(user)
                        updateUser(me)
                    }else{
                        if (activity is RPSActivity) {
                            activity.hideProgressDialog()
                            activity.showErrorSnackBar("Invalid action", true)
                        }
                    }
                }
            }.addOnFailureListener {
                if (activity is RPSActivity) {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(it.message.toString(), true)
                }
            }
    }

    private fun updateUser(users: Users?) {
        mFireStore.collection(Constants.USERNAMES)
            .document(getUserID())
            .set(users!!)
            .addOnSuccessListener {
                if (activity is RPSActivity) {
                    activity.friendAdded()
                }
            }.addOnFailureListener {
                if (activity is RPSActivity) {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(it.message.toString(), true)
                }
            }
    }

}