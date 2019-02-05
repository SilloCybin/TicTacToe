package com.example.cedricdevries.tictactoerecoveryapp;

import java.io.Serializable;

public class User implements Serializable {

    private String mUsername;
    private String mUserID;
    private String mTokenID;

    public User(){

    }

    public User(String username, String userid, String token){
        this.mUserID = userid;
        this.mUsername = username;
        this.mTokenID = token;
    }

    public String getmUserID() {
        return mUserID;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmTokenID() {
        return mTokenID;
    }


    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmTokenID(String mTokenID) {
        this.mTokenID = mTokenID;
    }
}
