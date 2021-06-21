package com.launcher.foodlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.model.Userss;

public class SignUpActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference users;

    FirebaseAuth fAuth;

    EditText mFullName, mEmail, mPassword, mPhone;
    Button btnSignUp;
    TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //firebase

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        mFullName = (EditText) findViewById(R.id.input_fullname);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mPhone = (EditText) findViewById(R.id.input_phone);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        login = findViewById(R.id.login_here);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), PermissionsActivity.class));
            finish();
        }


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                final Userss userss = new Userss(mFullName.getText().toString(),
                        mPassword.getText().toString(),
                        mPhone.getText().toString(),
                        mEmail.getText().toString()
                );

                //Register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "User created.", Toast.LENGTH_SHORT).show();

                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(userss.getUsername()).exists())
                                    Toast.makeText(SignUpActivity.this, "This Username Already Exists!", Toast.LENGTH_SHORT).show();
                                else {
                                    users.child(fAuth.getCurrentUser().getUid()).setValue(userss);
                                    Toast.makeText(SignUpActivity.this, "Successful SignUp", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                login.setOnClickListener(v1 -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(SignUpActivity.this, PermissionsActivity.class));
        }
    }

}