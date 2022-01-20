package com.example.hanger.ui.loginRegister;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanger.R;
import com.example.hanger.ui.helpers.ImageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class RegisterFragment extends Fragment {

    private Activity context;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private TextView tvLoginLink;
    private ImageView ivProfileImage;
    private ViewPager2 viewPager2;
    private ImageHelper imageHelper;

    public RegisterFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
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
        imageHelper = new ImageHelper(context);
        ActivityResultLauncher<String> mGetContent = this.registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            ivProfileImage.setImageBitmap(bitmap);
                            imageHelper.uploadImage(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        Button registerButton = view.findViewById(R.id.btn_register);
        emailEditText = view.findViewById(R.id.et_email_register);
        passwordEditText = view.findViewById(R.id.et_password_register);
        passwordConfirmEditText = view.findViewById(R.id.et_password_confirm);
        ivProfileImage = view.findViewById(R.id.iv_profile_picture);
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GaleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mGetContent.launch("image/*");
            }
        });

        tvLoginLink = view.findViewById(R.id.tv_login_link);
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager2.setCurrentItem(0, true);
            }
        });

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
            Log.e(TAG, "createAccount: password mismatch", e);

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
            viewPager2.setCurrentItem(0, true);
        } else {
            Toast.makeText(context, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}