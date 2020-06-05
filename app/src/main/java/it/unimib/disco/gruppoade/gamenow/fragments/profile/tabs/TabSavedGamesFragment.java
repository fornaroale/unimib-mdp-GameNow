package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import it.unimib.disco.gruppoade.gamenow.R;

public class TabSavedGamesFragment extends Fragment {

    public TabSavedGamesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_tab_saved_games, container, false);

        return root;
    }
}