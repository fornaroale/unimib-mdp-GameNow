package it.unimib.disco.gruppoade.gamenow.fragments.feed;

import android.content.Intent;
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
import it.unimib.disco.gruppoade.gamenow.activities.UserInitializationActivity;
import it.unimib.disco.gruppoade.gamenow.adapters.NewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;
import it.unimib.disco.gruppoade.gamenow.fragments.shared.NewsViewModel;

public class FeedFragment extends Fragment {

    private List<PieceOfNews> newsList;
    private NewsViewModel viewModel;
    private User user;
    private NewsListAdapter adapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyTV;
    private boolean feedInitializedSentinel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedInitializedSentinel = false;

        viewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);

        mSwipeRefreshLayout = view.findViewById(R.id.feed_swipe_refresh);
        mSwipeRefreshLayout.setRefreshing(true);
        mEmptyTV = view.findViewById(R.id.feed_empty_view);
        mRecyclerView = view.findViewById(R.id.feed_recycler_view);
        mEmptyTV.setText(R.string.news_loading);

        // Recupero dati database
        user = null;

        // Collego un listener all'utente
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);
    }

    public void initializeFeed(){
        newsList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new NewsListAdapter(getActivity(), newsList, user, (byte) 1);
        mRecyclerView.setAdapter(adapter);

        // Observer su oggetto LiveData (collezione news)
        final Observer<ArrayList<PieceOfNews>> observer = changedNewsList -> {
            newsList.clear();
            newsList.addAll(changedNewsList);
            selectNews(newsList);

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
        };

        LiveData<ArrayList<PieceOfNews>> liveData = viewModel.getNews();
        liveData.observe(getViewLifecycleOwner(), observer);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            viewModel.refreshNews();
        });
    }

    private void selectNews(List<PieceOfNews> newsRaw){
        if(user.getTags() != null) {
            List<String> userTags = user.getTags();

            for (int i = 0; i < newsRaw.size(); i++) {
                boolean keepArticle = false;

                String[] articlePlatforms = newsRaw.get(i).getProvider().getPlatform().split(",");
                for (String platform : articlePlatforms) {
                    if (userTags.contains(platform)) {
                        keepArticle = true;
                        break;
                    }
                }

                if (!keepArticle) {
                    newsRaw.remove(i);
                    i--;
                }
            }
        }
    }

    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            if(!FbDatabase.getUserDeleting()) {
                if (!feedInitializedSentinel && user != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    initializeFeed();
                    feedInitializedSentinel = true;
                } else if (user == null) {
                    Intent userInitializationIntent = new Intent(getActivity(), UserInitializationActivity.class);
                    startActivity(userInitializationIntent);
                }
                if (adapter != null) {
                    if (newsList != null) {
                        selectNews(newsList);
                        if (newsList.isEmpty()) {
                            mRecyclerView.setVisibility(View.GONE);
                            mEmptyTV.setText(R.string.no_data_available_feed);
                            mEmptyTV.setVisibility(View.VISIBLE);
                        } else {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mEmptyTV.setVisibility(View.GONE);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };
}