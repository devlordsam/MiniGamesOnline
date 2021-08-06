package com.lordsam.minigamesonline.utils

class Users(){
    var username = ""
    var uid = ""
    var friend  = ArrayList<Users>()

    constructor(
        username :String,
        uid :String,
        friend :ArrayList<Users>
    ) : this(){
        this.username = username
        this.uid = uid
        this.friend = friend
    }
}


