package com.example.cedricdevries.tictactoerecoveryapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OnlineGameState implements Serializable {

    private String mIDCombination;
    private Boolean mGameOn;
    private String mPlayer1;
    private String mPlayer2;
    private String turn;
    private List<String> mBoard = new ArrayList<>();
    private Boolean mGameOver = false;
    private Boolean askedForNewGame = false;
    private Boolean p1clickedForNewGame = false;
    private Boolean p2clickedForNewGame = false;
    private Boolean tieFlag = false;
    private Boolean quitGame = false;
    private Boolean p1clickedToQuit = false;
    private Boolean p2clickedToQuit = false;
    private int p1wins = 0;
    private int p2wins = 0;
    private int ties =0 ;

    public OnlineGameState() {

        for (int i = 0; i < 9; i++) {
            this.mBoard.add(OnlineTicTacToeGame.OPEN_SPOT);
        }
    }

    public OnlineGameState(String combination, Boolean state, String player1, String player2, String pturn) {
        this.mIDCombination = combination;
        this.mGameOn = state;
        this.mPlayer1 = player1;
        this.mPlayer2 = player2;
        this.turn = pturn;

        for (int i = 0; i < 9; i++) {
            this.mBoard.add(OnlineTicTacToeGame.OPEN_SPOT);
        }

    }

    public Boolean getmGameOn() {
        return mGameOn;
    }

    public String getmIDCombination() {
        return mIDCombination;
    }

    public String getmPlayer1() {
        return mPlayer1;
    }

    public String getmPlayer2() {
        return mPlayer2;
    }

    public String getTurn() {
        return turn;
    }

    public List<String> getmBoard() {
        return mBoard;
    }

    public Boolean getmGameOver() {
        return mGameOver;
    }

    public Boolean getAskedForNewGame() {
        return askedForNewGame;
    }

    public Boolean getP1clickedForNewGame() {
        return p1clickedForNewGame;
    }

    public Boolean getP2clickedForNewGame() {
        return p2clickedForNewGame;
    }

    public Boolean getTieFlag() {
        return tieFlag;
    }

    public Boolean getQuitGame() {
        return quitGame;
    }

    public int getP1wins() {
        return p1wins;
    }

    public int getP2wins() {
        return p2wins;
    }

    public int getTies() {
        return ties;
    }

    public Boolean getP1clickedToQuit() {
        return p1clickedToQuit;
    }

    public Boolean getP2clickedToQuit() {
        return p2clickedToQuit;
    }

    public void setmGameOn(Boolean mGameState) {
        this.mGameOn = mGameState;
    }

    public void setmIDCombination(String mIDCombination) {
        this.mIDCombination = mIDCombination;
    }

    public void setmPlayer1(String player) {
        this.mPlayer1 = player;
    }

    public void setmPlayer2(String player) {
        this.mPlayer2 = player;
    }

    public void setTurn(String pturn) {
        this.turn = pturn;
    }

    public void setmBoard(List<String> mBoardToInsert) {
        for (int l = 0; l < 9; l++) {
            mBoard.set(l, mBoardToInsert.get(l));
        }
    }

    public void setmGameOver(Boolean mGameOver) {
        this.mGameOver = mGameOver;
    }

    public void setAskedForNewGame(Boolean askedForNewGame) {
        this.askedForNewGame = askedForNewGame;
    }

    public void setP1clickedForNewGame(Boolean p1clickedForNewGame) {
        this.p1clickedForNewGame = p1clickedForNewGame;
    }

    public void setP2clickedForNewGame(Boolean p2clickedForNewGame) {
        this.p2clickedForNewGame = p2clickedForNewGame;
    }

    public void setTieFlag(Boolean tieFlag) {
        this.tieFlag = tieFlag;
    }

    public void setQuitGame(Boolean quitGame) {
        this.quitGame = quitGame;
    }

    public void setP1wins(int p1wins) {
        this.p1wins = p1wins;
    }

    public void setP2wins(int p2wins) {
        this.p2wins = p2wins;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public void setP1clickedToQuit(Boolean p1clickedToQuit) {
        this.p1clickedToQuit = p1clickedToQuit;
    }

    public void setP2clickedToQuit(Boolean p2clickedToQuit) {
        this.p2clickedToQuit = p2clickedToQuit;
    }
}

