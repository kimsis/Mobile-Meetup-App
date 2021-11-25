package com.example.hanger.ui.loginRegister;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanger.MainActivity;
import com.example.hanger.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private TextView emailField;
    private TextView passwordField;
    private Activity context;

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance(FirebaseUser user) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        auth = FirebaseAuth.getInstance();

        emailField = view.findViewById(R.id.et_email);
        passwordField = view.findViewById(R.id.et_password);

        Button signButton = view.findViewById(R.id.btn_sign_in);
        signButton.setOnClickListener(x -> singIn());
    }

    @Override
    public void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null)
            navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }

    private void singIn(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( context, this::handleSignInResult);

    }

    private void handleSignInResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            navigateToMainActivity();
        } else {
            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}