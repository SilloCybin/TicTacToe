package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

public class FindPlayerActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private Button mSignOut;
    private Firebase mDbCon;
    private FirebaseUser mCurrentUser;
    private String mCurrentUserString;
    private String mCurrentUserID;
    private String mTokenID;
    private ListView mUserListView;
    private ArrayList<String> mUserList = new ArrayList<>();
    private DatabaseReference mDbRef;
    private DatabaseReference mDbRefChild;
    private DatabaseReference mDbRef2;
    private DatabaseReference mDbRef2Child;
    private DatabaseReference mDbRef3;
    private DatabaseReference mDbRef3Child;
    private DatabaseReference mDbRef4;
    private User mDbUser;
    private User opponent;
    private Query query;
    private Query query2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_player);

        mUserListView = findViewById(R.id.listview);
        mSignOut = findViewById(R.id.sign_out);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserID = mCurrentUser.getUid();
        mCurrentUserString = mCurrentUser.getDisplayName();

        mTokenID = FirebaseInstanceId.getInstance().getToken();

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(FindPlayerActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                mTokenID = instanceIdResult.getToken();
            }
        });*/

        mDbUser = new User(mCurrentUserString, mCurrentUserID, mTokenID);

        // If the user is signing up, add profile to database :
        query = FirebaseDatabase.getInstance().getReference("signed_in_users").orderByChild("uid").equalTo(mCurrentUserID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){

                    mDbRef = FirebaseDatabase.getInstance().getReference("signed_in_users");
                    mDbRefChild = mDbRef.push();
                    mDbRefChild.setValue(mCurrentUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // If the user is already connected, find its twin in the database, delete it and set user once again.
        // FCM token added to user info to make it reachable by notifications
        mDbRef2 = FirebaseDatabase.getInstance().getReference("connected_users");
        mDbRef2Child = mDbRef2.push();

        query2 = FirebaseDatabase.getInstance().getReference("connected_users").orderByChild("mUserID").equalTo(mCurrentUserID);

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnap: dataSnapshot.getChildren()){

                    childSnap.getRef().removeValue();
                }
                    mDbRef2Child.setValue(mDbUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mUserList);
        mUserListView.setAdapter(arrayAdapter);

        // Display list of connected users :
        mDbRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User displayedUser = dataSnapshot.getValue(User.class);

                if(!displayedUser.getmUserID().equals(mDbUser.getmUserID())) {
                    mUserList.add(displayedUser.getmUsername());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                User deletedUser = dataSnapshot.getValue(User.class);

                mUserList.remove(deletedUser.getmUsername());
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(FindPlayerActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        // Sign out and remove the user as connected from the database
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();
                mDbRef2Child.removeValue().equals(mDbUser);
            }
        });

        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = (String) parent.getItemAtPosition(position);

                query = FirebaseDatabase.getInstance().getReference("connected_users").orderByChild("mUsername").equalTo(selectedItem);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnap: dataSnapshot.getChildren()){

                            opponent = childSnap.getValue(User.class);
                            PlayNotification notif = new PlayNotification(mCurrentUserID, opponent.getmUserID(), mCurrentUserString, opponent.getmUsername(), opponent.getmTokenID());

                            mDbRef3 = FirebaseDatabase.getInstance().getReference("notifications");
                            mDbRef3Child = mDbRef3.push();
                            mDbRef3Child.setValue(notif);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

            mDbRef4 = FirebaseDatabase.getInstance().getReference("online_games");

            mDbRef4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(opponent != null) {

                        String mIDCombinationTest = mCurrentUserID.concat("_" + opponent.getmUserID());

                        for (DataSnapshot childSnap: dataSnapshot.getChildren()) {

                            OnlineGameState notificationReply = childSnap.getValue(OnlineGameState.class);

                            if (notificationReply.getmIDCombination().equals(mIDCombinationTest)) {

                                if (notificationReply.getmGameOn()) {

                                    Toast.makeText(getApplicationContext(), opponent.getmUsername() + " accepted your challenge :D !", Toast.LENGTH_SHORT).show();

                                    Intent toOnlineTicTacToeActivity = new Intent(FindPlayerActivity.this, OnlineTicTacToeActivity.class);
                                    toOnlineTicTacToeActivity.putExtra("online_game", notificationReply);
                                    startActivity(toOnlineTicTacToeActivity);
                                    finish();

                                } else {

                                    Toast.makeText(getApplicationContext(), opponent.getmUsername() + " rejected your challenge :( !", Toast.LENGTH_SHORT).show();
                                    childSnap.getRef().removeValue();

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

        mDbRef = null;
        mDbRef2 = null;
        mDbRef3 = null;
        mDbRef4 = null;
        query = null;
        query2 = null;
        opponent = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

        mDbRef = null;
        mDbRef2 = null;
        mDbRef3 = null;
        mDbRef4 = null;
        query = null;
        query2 = null;
        opponent = null;
    }
}