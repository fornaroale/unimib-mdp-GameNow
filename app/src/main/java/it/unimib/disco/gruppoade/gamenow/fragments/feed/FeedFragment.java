package it.unimib.disco.gruppoade.gamenow.fragments.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.NewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.fragments.shared.DiscoverFeedViewModel;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    private List<PieceOfNews> newsList;
    private DiscoverFeedViewModel sharedFeedViewModel;
    private User user;
    private LiveData<ArrayList<PieceOfNews>> liveData;
    private NewsListAdapter adapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyTV;
    private boolean feedInitializedSentinel;
    private boolean requestedUpdate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedInitializedSentinel = false;
        requestedUpdate = false;

        sharedFeedViewModel = new ViewModelProvider(getActivity()).get(DiscoverFeedViewModel.class);
        sharedFeedViewModel.setFeedUse(true);

        mSwipeRefreshLayout = getView().findViewById(R.id.feed_swipe_refresh);
        mSwipeRefreshLayout.setRefreshing(true);
        mEmptyTV = getView().findViewById(R.id.feed_empty_view);
        mRecyclerView = getView().findViewById(R.id.feed_recycler_view);
        mEmptyTV.setText(R.string.news_loading);

        // Recupero dati database
        user = null;
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);

        if(user!=null) {
            mSwipeRefreshLayout.setRefreshing(true);
            initializeFeed();
            feedInitializedSentinel = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestedUpdate = true;
    }

    @Override
    public void onPause() {
        sharedFeedViewModel.cleanNews();
        super.onPause();
    }

    public void initializeFeed(){
        newsList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new NewsListAdapter(getActivity(), newsList, user, false);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Observer su oggetto LiveData (collezione news)
        final Observer<ArrayList<PieceOfNews>> observer = new Observer<ArrayList<PieceOfNews>>() {
            @Override
            public void onChanged(ArrayList<PieceOfNews> changedNewsList) {
                if(requestedUpdate) {
                    Log.d(TAG, "UTENTE --> CHANGED DATA!!!");

                    newsList.clear();
                    newsList.addAll(changedNewsList);

                    if (newsList.isEmpty()) {
                        mRecyclerView.setVisibility(View.GONE);
                        mEmptyTV.setText(R.string.no_data_available_feed);
                        mEmptyTV.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyTV.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);

                    requestedUpdate = false;
                }
            }
        };

        liveData = sharedFeedViewModel.getNews(user);
        liveData.observe(getViewLifecycleOwner(), observer);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestedUpdate = true;
                mSwipeRefreshLayout.setRefreshing(true);
                sharedFeedViewModel.getNews(user);
            }
        });
    }

    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            if(!feedInitializedSentinel) {
                mSwipeRefreshLayout.setRefreshing(true);
                initializeFeed();
                feedInitializedSentinel = true;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };
}