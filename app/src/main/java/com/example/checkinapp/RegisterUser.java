package com.example.checkinapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ProgressBar progressBarRegister;
    private EditText editTextFullName, editTextPhoneNum, editTextEmail, editTextPassword;
    private TextView registerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextPhoneNum = (EditText) findViewById(R.id.phoneNum);
        editTextPassword = (EditText) findViewById(R.id.password);

        registerUser = (TextView) findViewById(R.id.registerBtn);
        registerUser.setOnClickListener(this);

        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerBtn:
                
                
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phoneNum = editTextPhoneNum.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        // Add check for pass == confirmPass && also that it meets standard pass regEx
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum password length is 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBarRegister.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, phoneNum, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        progressBarRegister.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(RegisterUser.this, "Registration Unsuccessful. Please try again", Toast.LENGTH_LONG).show();
                                        progressBarRegister.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterUser.this, "Registration Unsuccessful. Please try again", Toast.LENGTH_LONG).show();
                            progressBarRegister.setVisibility(View.GONE);
                        }
                    }
                });
    }
}