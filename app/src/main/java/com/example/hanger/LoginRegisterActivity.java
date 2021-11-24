package com.example.hanger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView emailField;
    private TextView passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        auth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.et_email);
        passwordField = findViewById(R.id.et_password);

        Button signButton = findViewById(R.id.btn_sign_in);
        signButton.setOnClickListener(x -> singIn());
    }

    @Override
    public void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null)
            navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void singIn(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this::handleSignInResult);

    }

    private void handleSignInResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            navigateToMainActivity();
        } else {
            Toast.makeText(LoginRegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}