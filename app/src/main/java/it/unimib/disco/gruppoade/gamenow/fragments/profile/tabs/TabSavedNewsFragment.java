package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.NewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSavedNewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mEmptyTV;
    private NewsListAdapter adapter;
    private List<PieceOfNews> locallySavedNews;

    // Firebase
    private User user;
    private ValueEventListener postListenerFirstUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            // JSON to PieceOfNews Array
            locallySavedNews.clear();
            Gson gson = new Gson();
            for (String jsonPON : user.getNews()) {
                locallySavedNews.add(gson.fromJson(jsonPON, PieceOfNews.class));
            }

            adapter = new NewsListAdapter(getActivity(), locallySavedNews, user, true, new NewsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(PieceOfNews pieceOfNews) {

                }
            });

            // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
            if (locallySavedNews.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyTV.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyTV.setVisibility(View.GONE);
            }

            // Recupero il recyclerview dal layout xml e imposto l'adapter
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
    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            if(user!=null) {
                // JSON to PieceOfNews Array
                locallySavedNews.clear();
                Gson gson = new Gson();
                for (String jsonPON : user.getNews()) {
                    locallySavedNews.add(gson.fromJson(jsonPON, PieceOfNews.class));
                }

                // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
                if (locallySavedNews.isEmpty()) {
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

    public TabSavedNewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_saved_news, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inizializzo lista news mantenute salvate localmente
        locallySavedNews = new ArrayList<>();

        // Binding elementi visuali
        mRecyclerView = getView().findViewById(R.id.tabsavednews_recycler_view);
        mEmptyTV = getView().findViewById(R.id.tabsavednews_empty_view);

        // Recupero dati database
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListenerFirstUserData);
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);
    }
}