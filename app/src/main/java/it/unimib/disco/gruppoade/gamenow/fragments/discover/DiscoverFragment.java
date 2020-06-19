package it.unimib.disco.gruppoade.gamenow.fragments.discover;

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
import it.unimib.disco.gruppoade.gamenow.fragments.shared.NewsViewModel;

public class DiscoverFragment extends Fragment {

    private static final String TAG = "DiscoverFragment";

    private List<PieceOfNews> newsList;
    private NewsViewModel viewModel;
    private User user;
    private NewsListAdapter adapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyTV;
    private boolean discoverInitializedSentinel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        discoverInitializedSentinel = false;

        viewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);

        mSwipeRefreshLayout = getView().findViewById(R.id.discover_swipe_refresh);
        mSwipeRefreshLayout.setRefreshing(true);
        mEmptyTV = getView().findViewById(R.id.discover_empty_view);
        mRecyclerView = getView().findViewById(R.id.discover_recycler_view);
        mEmptyTV.setText(R.string.news_loading);

        // Recupero dati database
        user = null;
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);

        if(user!=null) {
            mSwipeRefreshLayout.setRefreshing(true);
            initializeDiscover();
            discoverInitializedSentinel = true;
        }
    }

    public void initializeDiscover(){
        newsList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new NewsListAdapter(getActivity(), newsList, user, (byte) 2);
        mRecyclerView.setAdapter(adapter);

        // Observer su oggetto LiveData (collezione news)
        final Observer<ArrayList<PieceOfNews>> observer = changedNewsList -> {

                Log.d(TAG, "OBSERVER DISCOVER --> CHANGED DATA!!!");
                Log.d(TAG, "OBSERVER USER --> " + user);

                newsList.clear();
                newsList.addAll(changedNewsList);
                selectNews(newsList);

                if (newsList.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyTV.setText(R.string.no_data_available_discover);
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
        Log.d(TAG, "UPD DISCOVER OBSERVING LIVEDATA --> " + liveData);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            viewModel.refreshNews();
        });
    }

    private void selectNews(List<PieceOfNews> newsRaw){
        if(user.getTags() != null) {
            List<String> userTags = user.getTags();

            for (int i = 0; i < newsRaw.size(); i++) {
                boolean delArticle = true;

                String[] articlePlatforms = newsRaw.get(i).getProvider().getPlatform().split(",");
                for (String platform : articlePlatforms) {
                    if (!userTags.contains(platform)) {
                        delArticle = false;
                    } else {
                        if(articlePlatforms.length==1){
                            delArticle = true;
                        }
                    }

                }

                if (delArticle) {
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
            if(!discoverInitializedSentinel) {
                mSwipeRefreshLayout.setRefreshing(true);
                initializeDiscover();
                discoverInitializedSentinel = true;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };
}