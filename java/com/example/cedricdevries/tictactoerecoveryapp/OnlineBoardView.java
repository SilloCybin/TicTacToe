package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class OnlineBoardView extends View {

    public static final int GRID_WIDTH = 6;
    private Bitmap mPlayer1Bitmap;
    private Bitmap mPLayer2Bitmap;
    private Paint mPaint;
    private OnlineTicTacToeGame mGame;

    public void setGame(OnlineTicTacToeGame game) {
        mGame = game;
    }

    public OnlineBoardView(Context context) {
        super(context);
        initialize();
    }

    public OnlineBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public OnlineBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize() {

        mPlayer1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        mPLayer2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cross);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        // Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);

        // Draw the board lines
        int cellWidth = boardWidth / 3;
        int cellHeight = boardHeight / 3;

        //vertical
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);

        //horizontal
        canvas.drawLine(0, cellHeight, boardWidth, cellHeight, mPaint);
        canvas.drawLine(0, cellHeight * 2, boardWidth, cellHeight * 2, mPaint);

        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {

            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int left = col * cellWidth;
            int top = row * cellHeight;
            int right = left + cellWidth;
            int bottom = top + cellHeight;

            if (mGame != null && mGame.getBoardOccupant(i).equals(OnlineTicTacToeGame.PLAYER_1)) {
                canvas.drawBitmap(mPlayer1Bitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            } else if (mGame != null && mGame.getBoardOccupant(i).equals(OnlineTicTacToeGame.PLAYER_2)) {
                canvas.drawBitmap(mPLayer2Bitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            }
        }

    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }

}