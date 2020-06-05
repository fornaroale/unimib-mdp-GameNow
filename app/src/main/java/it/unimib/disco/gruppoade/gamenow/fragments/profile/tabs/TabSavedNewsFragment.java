package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import it.unimib.disco.gruppoade.gamenow.adapters.SavedNewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;
import it.unimib.disco.gruppoade.gamenow.models.User;

public class TabSavedNewsFragment extends Fragment {

    private final String TAG = "TabSavedNews";

    private RecyclerView mRecyclerView;
    private SavedNewsListAdapter adapter;

    // Firebase
    private User user;
    private ValueEventListener postListenerFirstUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);

            // JSON to PieceOfNews Array
            List<PieceOfNews> locallySavedNews = new ArrayList<>();
            Gson gson = new Gson();
//            for(String jsonPON : user.getNews()){
//                locallySavedNews.add(gson.fromJson(jsonPON, PieceOfNews.class));
//            }

            // Controllo la presenza o meno di informazioni per mostrare un messaggio di stato
            if (locallySavedNews.isEmpty()) {
                getActivity().findViewById(R.id.recyclerView).setVisibility(View.GONE);
                getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            } else {
                getActivity().findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
            }

            // Recupero il recyclerview dal layout xml e imposto l'adapter
            mRecyclerView = getActivity().findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setHasFixedSize(true);
            adapter = new SavedNewsListAdapter(getActivity(), locallySavedNews, user);
            mRecyclerView.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, databaseError.getMessage());
            throw databaseError.toException();
        }
    };
    private ValueEventListener postListenerUserData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, databaseError.getMessage());
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
        final View root = inflater.inflate(R.layout.fragment_tab_saved_news, container, false);

        // Recupero dati database
        FbDatabase.getUserReference().addListenerForSingleValueEvent(postListenerFirstUserData);
        FbDatabase.getUserReference().addValueEventListener(postListenerUserData);

        return root;
    }
}