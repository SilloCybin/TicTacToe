package com.example.cedricdevries.tictactoerecoveryapp;

import java.util.ArrayList;
import java.util.List;

public class OnlineTicTacToeGame {

    public List<String> mBoard = new ArrayList<>();

    public static final String PLAYER_1 = "O";
    public static final String PLAYER_2 = "X";
    public static final String OPEN_SPOT = " " ;

    public OnlineTicTacToeGame(){
        mBoard.add("1");
        mBoard.add("2");
        mBoard.add("3");
        mBoard.add("4");
        mBoard.add("5");
        mBoard.add("6");
        mBoard.add("7");
        mBoard.add("8");
        mBoard.add("9");
    }

    public void clearBoard(){

        for(int i=0; i < mBoard.size(); i++){
            mBoard.set(i, OPEN_SPOT);
        }
    }

    public boolean setMove(String player, int location){

        if(mBoard.get(location).equals(OPEN_SPOT)){
            mBoard.set(location, player);
            return true;
        }
        else {return false;}
    }

    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard.get(i).equals(PLAYER_1) &&
                    mBoard.get(i+1).equals(PLAYER_1) &&
                    mBoard.get(i+2).equals(PLAYER_1))
                return 2;
            if (mBoard.get(i).equals(PLAYER_2) &&
                    mBoard.get(i+1).equals(PLAYER_2) &&
                    mBoard.get(i+2).equals(PLAYER_2))
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard.get(i).equals(PLAYER_1) &&
                    mBoard.get(i+3).equals(PLAYER_1) &&
                    mBoard.get(i+6).equals(PLAYER_1))
                return 2;
            if (mBoard.get(i).equals(PLAYER_2) &&
                    mBoard.get(i+3).equals(PLAYER_2) &&
                    mBoard.get(i+6).equals(PLAYER_2))
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard.get(0).equals(PLAYER_1) &&
                mBoard.get(4).equals(PLAYER_1) &&
                mBoard.get(8).equals(PLAYER_1) ||
                (mBoard.get(2).equals(PLAYER_1) &&
                        mBoard.get(4).equals(PLAYER_1) &&
                        mBoard.get(6).equals(PLAYER_1))))
            return 2;
        if ((mBoard.get(0).equals(PLAYER_2) &&
                mBoard.get(4).equals(PLAYER_2) &&
                mBoard.get(8).equals(PLAYER_2) ||
                (mBoard.get(2).equals(PLAYER_2) &&
                        mBoard.get(4).equals(PLAYER_2) &&
                        mBoard.get(6).equals(PLAYER_2))))
            return 3;

        // Check for tie
        for (int i = 0; i < mBoard.size(); i++) {
            // If we find a number, then no one has won yet
            if (!mBoard.get(i).equals(PLAYER_1) && !mBoard.get(i).equals(PLAYER_2))
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public String getBoardOccupant(int j){
        return mBoard.get(j);
    }

    public List<String> getmBoard(){
        return mBoard;
    }

    public void setmBoard(List<String> mBoardToInsert){
        for(int l=0; l < mBoard.size(); l++){
            mBoard.set(l,mBoardToInsert.get(l));
        }
    }
}
