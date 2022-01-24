package com.example.hanger.ui.connections;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanger.R;
import com.example.hanger.model.Connection;
import com.example.hanger.ui.helpers.ImageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    private ArrayList<Connection> connections;
    private Context context;
    private ImageHelper imageHelper;
    private StorageReference storageReference;
    FirebaseAuth auth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");


    public ConnectionsAdapter(ArrayList<Connection> connections, Context context) {
        this.connections = connections;
        this.context = context;
        imageHelper = new ImageHelper(context);
        storageReference = FirebaseStorage.getInstance("gs://hanger-1648c.appspot.com").getReference();
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;
        private final ImageView ivUserImage;
        private final Button btnMatch;
        private final Button btnChat;


        public ViewHolder(View view) {
            super(view);

            tvUsername = (TextView) view.findViewById(R.id.tv_username);
            ivUserImage = (ImageView) view.findViewById(R.id.iv_avatar);
            btnMatch = (Button) view.findViewById(R.id.btnUnmatch);
            btnChat = (Button) view.findViewById(R.id.btnChat);
        }

        public TextView getTextView() {
            return tvUsername;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View connectionView = inflater.inflate(R.layout.fragment_connection_item, viewGroup, false);

        // Return a new holder instance
        return new ViewHolder(connectionView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Connection connection = connections.get(position);

        // Set item views based on your views and data model
        TextView tvUsername = viewHolder.tvUsername;
        tvUsername.setText(connection.getUsername());
        ImageView ivUserImage = viewHolder.ivUserImage;
        imageHelper.getImage(ivUserImage, connection.getId());
        Button btnMatch = viewHolder.btnMatch;
        String matchText = "Match";
        if (connection.getIsMatched().equals("Matched")) {
            matchText = "Unmatch";
        }
        btnMatch.setText(matchText);
        btnMatch.setOnClickListener(v -> {
            DatabaseReference reference = database.getReference("locations/" + auth.getUid() + "/usersMatched/" + connection.getId());
            if (connection.getIsMatched().equals("Matched")) {
                connection.setIsMatched("Refused");
                reference.setValue("Refused");
                btnMatch.setText("Match");
            } else {
                connection.setIsMatched("Matched");
                reference.setValue("Matched");
                btnMatch.setText("Unmatch");
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return connections.size();
    }
}
