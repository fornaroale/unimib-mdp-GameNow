package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import it.unimib.disco.gruppoade.gamenow.models.Game;

import static android.view.View.GONE;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearcgFragment";

    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;
    private String body;
    private ComingSoonViewModel comingSoonViewModel;
    private Observer<List<Game>> observer;
    private LiveData<List<Game>> gamesList;
    private IncomingAdapter incomingAdapter;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String query = SearchFragmentArgs.fromBundle(getArguments()).getQuery();
        recyclerView = view.findViewById(R.id.search_recyclerview);
        lottieAnimationView = view.findViewById(R.id.search_animation_view);
        comingSoonViewModel = new ViewModelProvider(requireActivity()).get(ComingSoonViewModel.class);

        Log.d(TAG, "onCreate: " + query);
        body = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                "search \"" + query.toLowerCase() + "\";\n" +
                "limit 75;";
        incomingAdapter = new IncomingAdapter(getActivity(), getGameList(body), new IncomingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Game game) {
                SearchFragmentDirections.SearchDisplayGameInfo action = SearchFragmentDirections.searchDisplayGameInfo(game);
                Navigation.findNavController(view).navigate(action);
            }
        });
        recyclerView.setAdapter(incomingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        observer = new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                Log.d(TAG, "initRecyclerView: Init RecyclerView");
                if (games.isEmpty()){
                    CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinator);
                    Snackbar snackbar =  Snackbar.make( coordinatorLayout, "Nessun gioco trovato...", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();
                } if(games != null) {
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
                    incomingAdapter.setData(games);
                    lottieAnimationView.setVisibility(GONE);
                }
            }
        };
        gamesList = comingSoonViewModel.getMoreGames(body);
        gamesList.observe(getViewLifecycleOwner(), observer);
    }

    private List<Game> getGameList(String body){
        LiveData<List<Game>> gameList = comingSoonViewModel.getGames(body);
        if (gameList != null)
            return gameList.getValue();
        return null;
    }
}