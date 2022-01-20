package com.example.hanger.ui.loginRegister;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanger.LoginRegisterActivity;
import com.example.hanger.MainActivity;
import com.example.hanger.R;
import com.example.hanger.model.HangerUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth auth;
    private TextView emailField;
    private TextView passwordField;
    private Activity context;
    private TextView tvRegisterLink;
    private ViewPager2 viewPager2;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(FirebaseUser user) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public LoginFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
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
        tvRegisterLink = view.findViewById(R.id.tv_register_link);
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager2.setCurrentItem(1, true);
            }
        });

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

        if(!EmailValidator.isValidEmail(email)) {
            Toast.makeText(context, "Invalid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty() || password.length() < 6) {
            Toast.makeText(context, "Password must be at least 6 symbols!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( context, this::handleSignInResult);

    }

    private void handleSignInResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {

            DatabaseReference ref = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("locations/" + auth.getUid() );
            Log.d(TAG, "createAccount: " + ref);
            HangerUser currentUser = new HangerUser(auth.getUid());
            currentUser.setLatitude(0);
            currentUser.setLongitude(0);
            currentUser.setDiscoveryRadiusMeters(300);
            ref.setValue(currentUser);
            Log.d(TAG, "createAccount: " + ref + "       " + currentUser);

            navigateToMainActivity();
        } else {
            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}