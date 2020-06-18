package it.unimib.disco.gruppoade.gamenow.fragments.feed;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.NewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class FeedFragment extends Fragment {

    private static final String TAG = "DiscoverFragment";

    private List<PieceOfNews> newsList;
    private FeedViewModel feedViewModel;
    private User user;
    private boolean recyclerViewInitialized;
    private LiveData<ArrayList<PieceOfNews>> liveData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = getView().findViewById(R.id.feed_recycler_view);
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.feed_swipe_refresh);
        TextView mEmptyTV = getView().findViewById(R.id.feed_empty_view);

        // Recupero dati database
        FbDatabase.getUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                feedViewModel = new ViewModelProvider(getActivity()).get(FeedViewModel.class);
                feedViewModel.setFeedUse(true);
                feedViewModel.setUser(user);

                // Recupero il recyclerview dal layout xml e imposto l'adapter
                swipeRefreshLayout.setRefreshing(true);
                newsList = new ArrayList<>();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
                NewsListAdapter adapter = new NewsListAdapter(getActivity(), newsList, user, false, pieceOfNews -> {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                    builder.setShowTitle(true);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(getContext(), Uri.parse(pieceOfNews.getLink()));
                });
                mRecyclerView.setAdapter(adapter);

                // Observer su oggetto LiveData (collezione news)
                final Observer<ArrayList<PieceOfNews>> observer = new Observer<ArrayList<PieceOfNews>>(){
                    @Override
                    public void onChanged(ArrayList<PieceOfNews> changedNewsList) {
                        newsList.clear();
                        newsList.addAll(changedNewsList);

                        if(newsList.isEmpty()){
                            mRecyclerView.setVisibility(View.GONE);
                            mEmptyTV.setVisibility(View.VISIBLE);
                        } else {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mEmptyTV.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                };
                liveData = feedViewModel.getNews();
                liveData.observe(getViewLifecycleOwner(), observer);

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        liveData = feedViewModel.getNews();
                    }
                });

                FbDatabase.getUserReference().addValueEventListener(postListenerUserData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };
}