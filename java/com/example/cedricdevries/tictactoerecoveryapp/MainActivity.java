package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button button_hmpg = findViewById(R.id.button);
        Button button_online = findViewById(R.id.button2);

        button_hmpg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent_hmpg = new Intent (MainActivity.this, TicTacToeActivity.class);
                startActivity(intent_hmpg);

            }
        });

        button_online.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent_sign_in = new Intent (MainActivity.this, SignInActivity.class);
                startActivity(intent_sign_in);

            }
        });
    }
}
