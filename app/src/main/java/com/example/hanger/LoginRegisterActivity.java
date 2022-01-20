package com.example.hanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.hanger.ui.helpers.ViewPagerFragmentAdapter;
import com.example.hanger.ui.loginRegister.LoginFragment;
import com.example.hanger.ui.loginRegister.RegisterFragment;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class LoginRegisterActivity extends AppCompatActivity {

    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager2 viewPager2;
    private Method functionToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setContentView(R.layout.activity_login_register);


        //Finding the viewPager in the XML file
        this.viewPager2 = findViewById(R.id.ViewPager);

        fragments.add(new LoginFragment(this.viewPager2));
        fragments.add(new RegisterFragment(this.viewPager2));

        //Creating the Adapter object, to be used for the fragments
        ViewPagerFragmentAdapter viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this, fragments);

        //Setting the adapter on the ViewPager, so that it can properly display the Fragments
        this.viewPager2.setAdapter(viewPagerFragmentAdapter);
    }
}