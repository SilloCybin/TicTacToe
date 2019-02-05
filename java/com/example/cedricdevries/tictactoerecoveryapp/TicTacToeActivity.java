package com.example.cedricdevries.tictactoerecoveryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TicTacToeActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private TextView mInfoTextView;
    private Button mNewGameButton;
    private boolean mGameOver;
    private TextView humanscore;
    private TextView ties;
    private TextView computerscore;
    public int mHumanWins = 0;
    public int mTies = 0;
    public int mComputerWins = 0;
    static final int DIALOG_QUIT_ID = 1;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    enum player {human, computer}
    public player turn = player.human;
    private BoardView mBoardView;
    private char mGoFirst='h';
    private SharedPreferences mPrefs;
    public Boolean mSoundOn;
    public String difficultyLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tictactoe);

        mGame = new TicTacToeGame();

        mInfoTextView = (TextView) findViewById(R.id.information);

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        mSoundOn = mPrefs.getBoolean("mSoundOn", false);
        difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy))) {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        }
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder))) {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        }
        else {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }

        //mGame.setDifficultyLevel(mGame.DifficultyLevelToEnum(intdifflevel));

        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        ties = (TextView) findViewById(R.id.ties);
        ties.setText(R.string.ties);
        ties.append(Integer.toString(mTies));

        computerscore = (TextView) findViewById(R.id.computerscore);
        computerscore.setText(R.string.computerscore);
        computerscore.append(Integer.toString(mComputerWins));

        humanscore = (TextView) findViewById(R.id.humanscore);
        humanscore.setText(R.string.humanscore);
        humanscore.append(Integer.toString(mHumanWins));

        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.newgame2) {
            startNewGame();
            return true;
        } else if (item.getItemId() == R.id.settings) {
            startActivityForResult(new Intent(this, Settings.class), 0);
            return true;
        } else if (item.getItemId() == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        } else if (item.getItemId() == R.id.clear_score) {
            mHumanWins=0;
            humanscore.setText(R.string.humanscore);
            humanscore.append(Integer.toString(mHumanWins));
            mTies=0;
            ties.setText(R.string.ties);
            ties.append(Integer.toString(mTies));
            mComputerWins=0;
            computerscore.setText(R.string.computerscore);
            computerscore.append(Integer.toString(mComputerWins));
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
                                TicTacToeActivity.this.finish();
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
        mInfoTextView.setText(R.string.first_human);
        turn = player.human;
        mGameOver = false;
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            if (mSoundOn) {
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    mHumanMediaPlayer.start();
                } else if (player == TicTacToeGame.COMPUTER_PLAYER) {
                    mComputerMediaPlayer.start();
                }
            }
            mBoardView.invalidate();   // Redraw the board
            return true;
        }
        return false;
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (turn==player.human) {
                setTurn(player.human);
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {

                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();

                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        turn = player.computer;
                        setTurn(player.computer);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                int move = mGame.getComputerMove();
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                mBoardView.invalidate();
                                if (mSoundOn) {
                                    mComputerMediaPlayer.start();
                                }
                                int winner = mGame.checkForWinner();

                                if (winner == 0) {
                                    mInfoTextView.setText(R.string.turn_human);
                                    turn=player.human;
                                    setTurn(player.human);
                                } else if (winner == 1) {
                                    mInfoTextView.setText(R.string.result_tie);
                                    mGameOver = true;
                                    mTies++;
                                    ties.setText(R.string.ties);
                                    ties.append(Integer.toString(mTies));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                } else if (winner == 2) {
                                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                                    mGameOver = true;
                                    mHumanWins++;
                                    humanscore.setText(R.string.humanscore);
                                    humanscore.append(Integer.toString(mHumanWins));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                } else {
                                    mInfoTextView.setText(R.string.result_computer_wins);
                                    mGameOver = true;
                                    mComputerWins++;
                                    computerscore.setText(R.string.computerscore);
                                    computerscore.append(Integer.toString(mComputerWins));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                }
                            }

                        }, 2000);

                    }
                    else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        mGameOver = true;
                        mTies++;
                        ties.setText(R.string.ties);
                        ties.append(Integer.toString(mTies));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    } else if (winner == 2) {
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                        mGameOver = true;
                        mHumanWins++;
                        humanscore.setText(R.string.humanscore);
                        humanscore.append(Integer.toString(mHumanWins));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    } else {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver = true;
                        mComputerWins++;
                        computerscore.setText(R.string.computerscore);
                        computerscore.append(Integer.toString(mComputerWins));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    }

                }

            }

            else {
                setTurn(player.computer);
                turn=player.computer;
                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();

                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                int move = mGame.getComputerMove();
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                mBoardView.invalidate();
                                if (mSoundOn) {
                                    mComputerMediaPlayer.start();
                                }
                                int winner = mGame.checkForWinner();

                                if (winner == 0) {
                                    mInfoTextView.setText(R.string.turn_human);
                                    turn=player.human;
                                    setTurn(player.human);
                                } else if (winner == 1) {
                                    mInfoTextView.setText(R.string.result_tie);
                                    mGameOver = true;
                                    mTies++;
                                    ties.setText(R.string.ties);
                                    ties.append(Integer.toString(mTies));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                } else if (winner == 2) {
                                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                                    mGameOver = true;
                                    mHumanWins++;
                                    humanscore.setText(R.string.humanscore);
                                    humanscore.append(Integer.toString(mHumanWins));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                } else {
                                    mInfoTextView.setText(R.string.result_computer_wins);
                                    mGameOver = true;
                                    mComputerWins++;
                                    computerscore.setText(R.string.computerscore);
                                    computerscore.append(Integer.toString(mComputerWins));
                                    mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                                    mNewGameButton.setText(R.string.newgame);
                                    mNewGameButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mGameOver) {
                                                startNewGame();
                                                mNewGameButton.setText(R.string.blank);
                                            }
                                        }
                                    });
                                }
                            }

                        }, 2000);

                    }
                    else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        mGameOver = true;
                        mTies++;
                        ties.setText(R.string.ties);
                        ties.append(Integer.toString(mTies));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    } else if (winner == 2) {
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                        mGameOver = true;
                        mHumanWins++;
                        humanscore.setText(R.string.humanscore);
                        humanscore.append(Integer.toString(mHumanWins));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    } else {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver = true;
                        mComputerWins++;
                        computerscore.setText(R.string.computerscore);
                        computerscore.append(Integer.toString(mComputerWins));
                        mNewGameButton = (Button) findViewById(R.id.newgamebutton);
                        mNewGameButton.setText(R.string.newgame);
                        mNewGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mGameOver) {
                                    startNewGame();
                                    mNewGameButton.setText(R.string.blank);
                                }
                            }
                        });
                    }

            }

// So we aren't notified of continued events when finger is moved
            return false;
        }
    };



    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer_sound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_sound);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mComputerWins", Integer.valueOf(mComputerWins));
        outState.putInt("mTies", Integer.valueOf(mTies));
        outState.putString("difficulty_level", difficultyLevel);
        outState.putBoolean("mSoundOn", mSoundOn);

    }

    private void setTurn(player turn){
        if (turn==player.human){
            mGoFirst='h';
        }
        else mGoFirst='c';
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.putString("difficulty_level", difficultyLevel);
        ed.putBoolean("mSoundOn", mSoundOn);
        ed.commit();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("mSoundOn", false);

            difficultyLevel = mPrefs.getString("difficulty_level", getResources().getString(R.string.difficulty_harder));

            Log.d("hello", difficultyLevel);

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy))) {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            }
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder))) {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            }
            else {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
            }
        }
    }

}

