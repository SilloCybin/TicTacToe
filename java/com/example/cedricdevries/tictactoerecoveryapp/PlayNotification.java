package com.example.cedricdevries.tictactoerecoveryapp;

import java.io.Serializable;

public class PlayNotification implements Serializable {

    private String mSourceID;
    private String mSourceName;
    private String mDestinationID;
    private String mDestinationName;
    private String mDestinationToken;
    private String mIDCombination;


    public PlayNotification(){

    }

    public PlayNotification(String source, String destination, String sname, String dname, String dtoken){

        this.mSourceID = source;
        this.mSourceName = sname;
        this.mDestinationID = destination;
        this.mDestinationName = dname;
        this.mDestinationToken = dtoken;

        this.mIDCombination = mSourceID.concat("_" + mDestinationID);

    }

    public String getmSourceID() {
        return mSourceID;
    }

    public String getmSourceName(){return mSourceName;}

    public String getmDestinationID() {
        return mDestinationID;
    }

    public String getmDestinationName(){return mDestinationName;}

    public String getmDestinationToken(){return mDestinationToken;}

    public String getmIDCombination(){return mIDCombination;}


    public void setmSourceID(String source) {
        this.mSourceID = source;
    }

    public void setmDestinationID(String destination) {
        this.mDestinationID = destination;
    }

    public void setmSourceName(String sname){
        this.mSourceName = sname;
    }

    public void setmDestinationName(String dname){
        this.mDestinationName = dname;
    }

    public void setmDestinationToken(String dtoken){
        this.mDestinationToken = dtoken;
    }

    public void setmIDCombination(String mIDCombination) {
        this.mIDCombination = mIDCombination;
    }
}
