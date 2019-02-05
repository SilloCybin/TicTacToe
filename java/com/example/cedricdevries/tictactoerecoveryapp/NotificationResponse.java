package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class NotificationResponse extends AppCompatActivity {

    private TextView mTitleView;
    private TextView mBodyView;
    private Button mAcceptButton;
    private Button mRejectButton;
    private FirebaseUser mCurrentUser;
    private String mCurrentUserID;
    private String mIDCombination;
    private DatabaseReference mRef;
    private DatabaseReference mRefChild;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_response);

        mTitleView = findViewById(R.id.notif_title);
        mBodyView = findViewById(R.id.notif_body);
        mAcceptButton = findViewById(R.id.accept_button);
        mRejectButton = findViewById(R.id.reject_button);

        String dataTitle = getIntent().getStringExtra("title");
        String dataBody = getIntent().getStringExtra("body");
        final String dataNotifSenderID = getIntent().getStringExtra("notifSenderID");
        final String dataNotifSenderName = getIntent().getStringExtra("notifSenderName");
        final String dataNotifRecipientName = getIntent().getStringExtra("notifRecipientName");

        mTitleView.setText(dataTitle);
        mBodyView.setText(dataBody);

        //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                mCurrentUserID = mCurrentUser.getUid();
                mIDCombination = dataNotifSenderID.concat("_" + mCurrentUserID);

                query = FirebaseDatabase.getInstance().getReference("notifications").orderByChild("mIDCombination").equalTo(mIDCombination);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot childSnap: dataSnapshot.getChildren()){
                            childSnap.getRef().removeValue();
                        }

                        OnlineGameState mGameState = new OnlineGameState(mIDCombination, true, dataNotifSenderName, dataNotifRecipientName, dataNotifSenderName);
                        // TO DO :

                        mRef = FirebaseDatabase.getInstance().getReference("online_games");
                        mRefChild = mRef.push();
                        mRefChild.setValue(mGameState);

                        Toast.makeText(getApplicationContext(), "Challenge accepted", Toast.LENGTH_SHORT).show();

                        Intent toOnlineTicTacToeActivity = new Intent(NotificationResponse.this, OnlineTicTacToeActivity.class);
                        toOnlineTicTacToeActivity.putExtra("online_game", mGameState);
                        startActivity(toOnlineTicTacToeActivity);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                mCurrentUserID = mCurrentUser.getUid();
                mIDCombination = dataNotifSenderID.concat("_" + mCurrentUserID);

                query = FirebaseDatabase.getInstance().getReference("notifications").orderByChild("mIDCombination").equalTo(mIDCombination);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot childSnap: dataSnapshot.getChildren()){
                            childSnap.getRef().removeValue();
                        }

                        OnlineGameState mGameState = new OnlineGameState(mIDCombination, false, dataNotifSenderName, dataNotifRecipientName, dataNotifSenderName);

                        mRef = FirebaseDatabase.getInstance().getReference("online_games");
                        mRefChild = mRef.push();
                        mRefChild.setValue(mGameState);

                        Toast.makeText(getApplicationContext(), "Challenge rejected", Toast.LENGTH_SHORT).show();

                        Intent toFindPlayerActivity = new Intent(NotificationResponse.this, FindPlayerActivity.class);
                        startActivity(toFindPlayerActivity);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });

    }

}
