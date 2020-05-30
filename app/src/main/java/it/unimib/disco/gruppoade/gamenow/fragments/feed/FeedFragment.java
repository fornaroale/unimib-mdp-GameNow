package it.unimib.disco.gruppoade.gamenow.fragments.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.unimib.disco.gruppoade.gamenow.ForumActivity;
import it.unimib.disco.gruppoade.gamenow.R;

public class FeedFragment extends Fragment {



    private FeedViewModel feedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        feedViewModel =
                ViewModelProviders.of(this).get(FeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_feed, container, false);



        Button button = root.findViewById(R.id.button_signOut);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();

                // close activity

            }
        });

        return root;
    }
}
