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
import com.example.hanger.databinding.FragmentLoginBinding;
import com.example.hanger.databinding.FragmentSettingsBinding;
import com.example.hanger.model.HangerUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth auth;
    private Activity context;
    private ViewPager2 viewPager2;
    private FragmentLoginBinding binding;

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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        binding.tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager2.setCurrentItem(1, true);
            }
        });

        binding.btnSignIn.setOnClickListener(x -> singIn());
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null)
            navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }

    private void singIn() {
        Log.d(TAG, "singIn: " + binding.etEmail + "    " + binding.etPassword + "    " + "hit");
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (!EmailValidator.isValidEmail(email)) {
            Toast.makeText(context, "Invalid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(context, "Password must be at least 6 symbols!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(context, this::handleSignInResult);

    }

    private void handleSignInResult(Task<AuthResult> loginTask) {

        if (!loginTask.isSuccessful()) {
            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");

        database.getReference("locations/" + auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HangerUser currentUser = snapshot.getValue(HangerUser.class);
                if (currentUser == null) {
                    DatabaseReference ref = database.getReference("locations/" + auth.getUid());
                    HangerUser userToCreate = new HangerUser();
                    userToCreate.setId(auth.getUid());
                    ref.setValue(userToCreate);
                }

                navigateToMainActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}