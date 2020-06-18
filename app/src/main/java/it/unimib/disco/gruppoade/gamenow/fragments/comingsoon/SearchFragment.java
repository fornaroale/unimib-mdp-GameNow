package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.User;

import static android.view.View.GONE;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearcgFragment";

    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;
    private String body;
    private SearchViewModel searchViewModel;
    private Observer<List<Game>> observer;
    private LiveData<List<Game>> gamesList;
    private IncomingAdapter incomingAdapter;
    private User user;
    private ActionBar actionBar;
    private ValueEventListener postListenerFirstUserData;

    private ValueEventListener postListenerUserData;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String query = SearchFragmentArgs.fromBundle(getArguments()).getQuery();
        recyclerView = view.findViewById(R.id.search_recyclerview);
        lottieAnimationView = view.findViewById(R.id.search_animation_view);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);



        postListenerFirstUserData = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                body = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "search \"" + query.toLowerCase() + "\";\n" +
                        "limit 75;";
                incomingAdapter = new IncomingAdapter(getActivity(), getGameList(body), new IncomingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Game game) {
                        SearchFragmentDirections.SearchDisplayGameInfo action = SearchFragmentDirections.searchDisplayGameInfo(game);
                        NavController navController = Navigation.findNavController(view);
                        Log.d(TAG, "onItemClick: Nav controller " + navController.getGraph());
                        Log.d(TAG, "onItemClick: Nav controller " + navController.getCurrentDestination());
                        navController.navigate(action);
                        Log.d(TAG, "onItemClick: Nav controller " + navController.getCurrentDestination());
                    }
                }, user);

                recyclerView.setAdapter(incomingAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

                observer = new Observer<List<Game>>() {
                    @Override
                    public void onChanged(List<Game> games) {
                        Log.d(TAG, "initRecyclerView: Init RecyclerView");
                        TextView giocoNA = view.findViewById(R.id.coordinator);
                        if (games.isEmpty()){
                            giocoNA.setVisibility(View.VISIBLE);
                            giocoNA.setText(R.string.nessun_gioco);
                        } else if(games != null) {
                            giocoNA.setVisibility(GONE);
                            giocoNA.setText("");
                            Collections.sort(games, new Comparator<Game>() {
                                @Override
                                public int compare(Game o1, Game o2) {
                                    if(o1.getDate() != null && o2.getDate() != null)
                                        return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
                                    if(o1.getDate() == null && o2.getDate() == null)
                                        return 0;
                                    if(o1.getDate() == null)
                                        return 1;
                                    return -1;
                                }

                            });
                        }
                        incomingAdapter.setData(games);
                        lottieAnimationView.setVisibility(GONE);
                    }
                };
                gamesList.observe(getViewLifecycleOwner(), observer);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        postListenerUserData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                incomingAdapter.notifyDataSetChanged();
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

    private List<Game> getGameList(String body){
        searchViewModel.resetGames();
        gamesList = searchViewModel.getGames(body);
        if (gamesList != null)
            return gamesList.getValue();
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        actionBar.setDisplayShowTitleEnabled(true);
    }
}