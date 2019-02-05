package com.example.cedricdevries.tictactoerecoveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword,inputUsername;
    private Button btnSignIn, btnSignUp;
    private FirebaseAuth auth;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.to_sign_in);
        btnSignUp = (Button) findViewById(R.id.register);
        inputEmail = (EditText) findViewById(R.id.sign_up_email);
        inputPassword = (EditText) findViewById(R.id.sign_up_password);
        inputUsername = (EditText) findViewById(R.id.sign_up_username);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUsername.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user only if its username isn't already taken :
                query = FirebaseDatabase.getInstance().getReference("signed_in_users").orderByChild("displayName").equalTo(username);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){

                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Toast.makeText(SignUpActivity.this, "Registration complete ! Click SIGN IN to sign in and play" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.

                                            if (!task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Authentication failed " + task.getException() + "or username is already taken",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {

                                                FirebaseUser user = auth.getCurrentUser();
                                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                                Log.d("username", username);
                                                if(user.updateProfile(profileUpdate).isSuccessful()){
                                                    Log.d("username2", "Success");
                                                }

                                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                finish();
                                            }
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Username already taken. Please choose another one", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SignUpActivity.this, "Database problem", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

}