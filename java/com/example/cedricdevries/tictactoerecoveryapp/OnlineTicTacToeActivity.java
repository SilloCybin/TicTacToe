package com.example.cedricdevries.tictactoerecoveryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OnlineTicTacToeActivity extends AppCompatActivity {

    private OnlineBoardView mBoardView;
    private OnlineGameState onlineGame;
    private OnlineTicTacToeGame mGame;

    private TextView mInfoTextView;
    private TextView player1score;
    private TextView ties;
    private TextView player2score;
    private Button mNewGameButton;

    private int mPlayer1Wins = 0;
    private int mTies = 0;
    private int mPlayer2Wins = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_SIGN_OUT_ID = 2;
    private boolean mGameOver;
    private boolean quitGame;
    private String turn;
    private String key;
    private String Player1;
    private String Player2;

    private Query query;
    private DatabaseReference mRef;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online_tictactoe);

        onlineGame = (OnlineGameState) getIntent().getSerializableExtra("online_game");
        Player1 = onlineGame.getmPlayer1();
        Player2 = onlineGame.getmPlayer2();

        mGame = new OnlineTicTacToeGame();

        mInfoTextView = findViewById(R.id.information);

        mNewGameButton = findViewById(R.id.newgamebutton);
        mNewGameButton.setEnabled(false);

        mBoardView = findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        ties = findViewById(R.id.ties);
        ties.setText(R.string.ties);
        ties.append(Integer.toString(mTies));

        player2score = findViewById(R.id.player_2_score);
        player2score.setText(Player2 + ": ");
        player2score.append(Integer.toString(mPlayer2Wins));

        player1score = findViewById(R.id.player_1_score);
        player1score.setText(Player1 + ": ");
        player1score.append(Integer.toString(mPlayer1Wins));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("online_games");

        query = FirebaseDatabase.getInstance().getReference("online_games").orderByChild("mIDCombination").equalTo(onlineGame.getmIDCombination());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnap: dataSnapshot.getChildren()) {

                    key = childSnap.getKey();

                    onlineGame = childSnap.getValue(OnlineGameState.class);

                    mGame.setmBoard(onlineGame.getmBoard());
                    mGameOver = onlineGame.getmGameOver();
                    quitGame = onlineGame.getQuitGame();
                    turn = onlineGame.getTurn();
                    mPlayer1Wins = onlineGame.getP1wins();
                    mPlayer2Wins = onlineGame.getP2wins();
                    mTies = onlineGame.getTies();

                    if(quitGame){

                        if(mCurrentUser.getDisplayName().equals(Player1) && !onlineGame.getP1clickedToQuit() || mCurrentUser.getDisplayName().equals(Player2) && !onlineGame.getP2clickedToQuit()) {

                            Toast.makeText(getApplicationContext(), "Your opponent left the game", Toast.LENGTH_LONG).show();

                            childSnap.getRef().removeValue();

                            Intent toFindPlayerActivity = new Intent(OnlineTicTacToeActivity.this, FindPlayerActivity.class);
                            startActivity(toFindPlayerActivity);
                            OnlineTicTacToeActivity.this.finish();
                        }
                    }

                    if(mGameOver) {

                        if (onlineGame.getAskedForNewGame()) {

                            if ((mCurrentUser.getDisplayName().equals(Player1) && !onlineGame.getP1clickedForNewGame()) || (mCurrentUser.getDisplayName().equals(Player2) && !onlineGame.getP2clickedForNewGame())) {

                                mNewGameButton.setEnabled(true);
                                mNewGameButton.setText(R.string.playagain);
                                mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        startNewGame();
                                        Toast.makeText(getApplicationContext(), "Here we go again", Toast.LENGTH_LONG).show();

                                        mNewGameButton.setEnabled(false);
                                        mNewGameButton.setText(R.string.blank);
                                    }
                                });
                            } else if ((mCurrentUser.getDisplayName().equals(Player1) && onlineGame.getP1clickedForNewGame()) || (mCurrentUser.getDisplayName().equals(Player2) && onlineGame.getP2clickedForNewGame())) {
                                mInfoTextView.setText(R.string.waiting_for_other_player_to_click);
                            }
                        } else {

                            enableNewGameButton();

                            if (!onlineGame.getTieFlag()) {

                                if (!turn.equals(mCurrentUser.getDisplayName())) {
                                    mInfoTextView.setText(R.string.result_you_lost);
                                } else {
                                    mInfoTextView.setText(R.string.result_you_won);
                                }
                            } else {
                            mInfoTextView.setText(R.string.result_tie);
                            }
                        }
                    } else {
                        if (!turn.equals(mCurrentUser.getDisplayName())) {

                            if(turn.equals(onlineGame.getmPlayer1())){
                                mInfoTextView.setText(Player1+"'s turn");
                            }
                            else{
                                mInfoTextView.setText(Player2+"'s turn");
                            }
                        }
                        else{
                            mInfoTextView.setText(R.string.your_turn);
                        }
                    }
                    mBoardView.invalidate();

                    player1score.setText(Player1 + ": ");
                    player1score.append(Integer.toString(mPlayer1Wins));

                    ties.setText(R.string.ties);
                    ties.append(" "+Integer.toString(mTies));

                    player2score.setText(Player2 + ": ");
                    player2score.append(Integer.toString(mPlayer2Wins));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_onlinemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        }
        if (item.getItemId() == R.id.sign_out) {
            showDialog(DIALOG_SIGN_OUT_ID);
            return true;
        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setNegativeButton(R.string.quit_no, null)
                        .setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                onlineGame.setQuitGame(true);

                                if(mCurrentUser.getDisplayName().equals(Player1)){
                                    onlineGame.setP1clickedToQuit(true);
                                }
                                else{
                                    onlineGame.setP2clickedToQuit(true);
                                }

                                Map<String, Object> childUpdate1 = new HashMap<>();
                                childUpdate1.put("/" + key + "/", onlineGame);
                                mRef.updateChildren(childUpdate1);

                                Intent toFindPlayerActivity = new Intent(OnlineTicTacToeActivity.this, FindPlayerActivity.class);
                                startActivity(toFindPlayerActivity);
                                OnlineTicTacToeActivity.this.finish();

                            }
                        });
                dialog = builder.create();

                break;

            case DIALOG_SIGN_OUT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.sign_out_question)
                        .setCancelable(false)
                        .setNegativeButton(R.string.sign_out_no, null)
                        .setPositiveButton(R.string.sign_out_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                onlineGame.setQuitGame(true);

                                if(mCurrentUser.getDisplayName().equals(Player1)){
                                    onlineGame.setP1clickedToQuit(true);
                                }
                                else{
                                    onlineGame.setP2clickedToQuit(true);
                                }

                                Map<String, Object> childUpdate1 = new HashMap<>();
                                childUpdate1.put("/" + key + "/", onlineGame);
                                mRef.updateChildren(childUpdate1);

                                Query query2 = FirebaseDatabase.getInstance().getReference("connected_users").orderByChild("mUsername").equalTo(mCurrentUser.getDisplayName());
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot childSnap: dataSnapshot.getChildren()){
                                            childSnap.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                auth = FirebaseAuth.getInstance();
                                auth.signOut();

                                Intent toMainActivity = new Intent(OnlineTicTacToeActivity.this, MainActivity.class);
                                startActivity(toMainActivity);
                                OnlineTicTacToeActivity.this.finish();
                            }
                        });
                dialog = builder.create();

                break;
        }

        return dialog;
    }

    private void startNewGame() {

        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver = false;

        if(onlineGame.getTurn().equals(Player1)){
            onlineGame.setTurn(Player2);
        }
        else {
            onlineGame.setTurn(Player1);
        }

        onlineGame.setmBoard(mGame.getmBoard());
        onlineGame.setmGameOver(mGameOver);
        onlineGame.setAskedForNewGame(false);
        onlineGame.setP1clickedForNewGame(false);
        onlineGame.setP2clickedForNewGame(false);
        onlineGame.setTieFlag(false);

        Map<String, Object> childUpdate1 = new HashMap<>();
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);

        turn = onlineGame.getTurn();
    }

    private boolean setMove(String player, int location) {

        if (mGame.setMove(player, location)) {
            synchronizeGame();
            mBoardView.invalidate();
            return true;
        }
        return false;
    }

    private void synchronizeGame(){

        Map<String, Object> childUpdate1 = new HashMap<>();
        onlineGame.setmBoard(mGame.getmBoard());
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);
    }

    private void setTurn(String player){

        Map<String, Object> childUpdate1 = new HashMap<>();
        onlineGame.setTurn(player);
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);
    }

    private void setScore(String type, int value){

        if (type.equals("tie")){
            onlineGame.setTies(value);
        }
        else if (type.equals("player1")){
            onlineGame.setP1wins(value);
        }
        else if (type.equals("player2")){
            onlineGame.setP2wins(value);
        }

        Map<String, Object> childUpdate1 = new HashMap<>();
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);
    }

    private void setGameOver(Boolean bool){

        Map<String, Object> childUpdate1 = new HashMap<>();
        onlineGame.setmGameOver(bool);
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);
    }

    private void setFlagForTie(Boolean bool){

        Map<String, Object> childUpdate1 = new HashMap<>();
        onlineGame.setTieFlag(bool);
        childUpdate1.put("/" + key + "/", onlineGame);
        mRef.updateChildren(childUpdate1);
    }

    private void enableNewGameButton(){
        mNewGameButton.setEnabled(true);
        mNewGameButton.setText(R.string.playagain);
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onlineGame.setAskedForNewGame(true);

                if(mCurrentUser.getDisplayName().equals(Player1)){
                    onlineGame.setP1clickedForNewGame(true);
                }
                else{
                    onlineGame.setP2clickedForNewGame(true);
                }

                Map<String, Object> childUpdate1 = new HashMap<>();
                childUpdate1.put("/" + key + "/", onlineGame);
                mRef.updateChildren(childUpdate1);

                mNewGameButton.setText(R.string.blank);
                mNewGameButton.setEnabled(false);
            }
        });
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (turn.equals(mCurrentUser.getDisplayName())) {

                if (turn.equals(onlineGame.getmPlayer1())) {

                    if (!mGameOver && setMove(OnlineTicTacToeGame.PLAYER_1, pos)) {

                        int winner = mGame.checkForWinner();

                        if (winner == 0) {
                            setTurn(Player2);

                        } else if (winner == 1) {
                            mInfoTextView.setText(R.string.result_tie);
                            mGameOver = true;
                            setFlagForTie(true);
                            setGameOver(true);
                            mTies++;
                            setScore("tie", mTies);

                            enableNewGameButton();

                        } else if (winner == 2) {
                            mGameOver = true;
                            setGameOver(true);
                            mPlayer1Wins++;
                            setScore("player1", mPlayer1Wins);

                            enableNewGameButton();
                        }
                    }
                } else {
                    if (!mGameOver && setMove(OnlineTicTacToeGame.PLAYER_2, pos)) {

                        // If no winner yet, let the computer make a move
                        int winner = mGame.checkForWinner();

                        if (winner == 0) {
                            setTurn(Player1);

                        } else if (winner == 1) {
                            mInfoTextView.setText(R.string.result_tie);
                            mGameOver = true;
                            setFlagForTie(true);
                            setGameOver(true);
                            mTies++;
                            setScore("tie", mTies);

                            enableNewGameButton();

                        } else if (winner == 3){
                            mGameOver = true;
                            setGameOver(true);
                            mPlayer2Wins++;
                            setScore("player2", mPlayer2Wins);

                            enableNewGameButton();
                        }
                    }
                }
                mBoardView.invalidate();
            }
// So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onStop(){
        super.onStop();
        mRef = null;
        query = null;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mRef = null;
        query = null;
    }

}