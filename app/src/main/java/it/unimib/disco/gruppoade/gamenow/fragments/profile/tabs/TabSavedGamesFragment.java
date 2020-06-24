package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.ProfileFragmentDirections;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSavedGamesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mEmptyTV;
    private IncomingAdapter adapter;
    private List<Game> locallySavedGames;
    private ActionBar actionBar;

    //Firebase
    private User user;

    public TabSavedGamesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowTitleEnabled(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_saved_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.saved_game_tab_recycler);
        mEmptyTV = view.findViewById(R.id.saved_game_tab_text);

        locallySavedGames = new ArrayList<>();

        ValueEventListener postListenerFirstUserData = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                locallySavedGames.clear();
                Gson gson = new Gson();
                for (String jsonGame : user.getGames()) {
                    locallySavedGames.add(gson.fromJson(jsonGame, Game.class));
                }

                adapter = new IncomingAdapter(getActivity(), locallySavedGames, game -> {
                    ProfileFragmentDirections.ActionNavigationProfileToGameInfoFragment action = ProfileFragmentDirections.actionNavigationProfileToGameInfoFragment(game);
                    NavController navController = NavHostFragment.findNavController(requireParentFragment());
                    navController.navigate(action);
                }, user);

                // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
                if (locallySavedGames.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyTV.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyTV.setVisibility(View.GONE);
                }

                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        ValueEventListener postListenerUserData = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    locallySavedGames.clear();
                    Gson gson = new Gson();
                    for (String jsonGame : user.getGames()) {
                        locallySavedGames.add(gson.fromJson(jsonGame, Game.class));
                    }

                    // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
                    if (locallySavedGames.isEmpty()) {
                        mRecyclerView.setVisibility(View.GONE);
                        mEmptyTV.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyTV.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        // Recupero dati database
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListenerFirstUserData);
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);
    }

    @Override
    public void onStart() {
        super.onStart();
        actionBar.setDisplayShowTitleEnabled(true);
    }
}