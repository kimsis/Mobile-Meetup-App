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

import com.example.hanger.ui.loginRegister.LoginFragment;
import com.example.hanger.ui.loginRegister.RegisterFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LoginRegisterActivity extends AppCompatActivity {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setContentView(R.layout.activity_login_register);

        fragments.add(new LoginFragment());
        fragments.add(new RegisterFragment());

        //Finding the viewPager in the XML file
        ViewPager2 viewPager = findViewById(R.id.ViewPager);

        //Creating the Adapter object, to be used for the fragments
        ViewPagerFragmentAdapter viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this);

        //Setting the adapter on the ViewPager, so that it can properly display the Fragments
        viewPager.setAdapter(viewPagerFragmentAdapter);
    }

    public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}