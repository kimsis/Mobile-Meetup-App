package com.example.hanger.ui.connections;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hanger.R;
import com.example.hanger.model.Connection;
import com.example.hanger.model.HangerUser;
import com.example.hanger.ui.settings.SettingsFragment;
import com.example.hanger.ui.connections.ConnectionsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectionsFragment extends Fragment {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");
    private final FirebaseAuth authentication = FirebaseAuth.getInstance();
    private HangerUser currentUser;
    private HashMap<String,HangerUser> allUsers;
    private Context context;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connections, container, false);
        context = getContext();
        getCurrentUser();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_connections);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        ConnectionsAdapter mAdapter = new ConnectionsAdapter(new ArrayList<Connection>(),context);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void getAllUsers()
    {
        database.getReference("locations/" ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HangerUser> fetchedUsers = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, HangerUser>>() {
                });
                if (fetchedUsers == null) {
                    showErrorToast("Could not find current user when trying to set location");
                    return;
                }
                allUsers = fetchedUsers;

                Map<String,String> users = currentUser.getUsersMatched();
                ArrayList<Connection> connections = new ArrayList<>();

                for (Map.Entry<String,String> user: users.entrySet()) {
                    String id = user.getKey();
                    String isMatched = user.getValue();
                    String name = allUsers.get(id).getName();
                    connections.add(new Connection(id,name,isMatched));
                }


                ConnectionsAdapter mAdapter = new ConnectionsAdapter(connections,context);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorToast("Failed to set user location:" + databaseError.getMessage());
            }
        });
    }

    private void getCurrentUser() {
        database.getReference("locations/" + authentication.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HangerUser fetchedUserLocation = dataSnapshot.getValue(HangerUser.class);
                if (fetchedUserLocation == null) {
                    showErrorToast("Could not find current user when trying to set location");
                    return;
                }
                currentUser = fetchedUserLocation;

                getAllUsers();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorToast("Failed to set user location:" + databaseError.getMessage());
            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}