package com.example.hanger.ui.loginRegister;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    private Activity context;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        Button registerButton = view.findViewById(R.id.btn_register);
        emailEditText = view.findViewById(R.id.et_email_register);
        passwordEditText = view.findViewById(R.id.et_password_register);
        passwordConfirmEditText = view.findViewById(R.id.et_password_confirm);

        registerButton.setOnClickListener(x -> createAccount());
    }

    private void createAccount() {
        // [START create_user_with_email]

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        try {
            FireBaseHub.CreateUser(email, password, passwordConfirm, this);
        } catch (PasswordMismatchException e) {
            Toast.makeText(context, "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "createAccount: password mismatch", e );

        } catch (InvalidEmailException e) {
            Toast.makeText(context, "Please enter a valid email.",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "createAccount: invalid email", e);
        }
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(context, "Authentication successful.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}