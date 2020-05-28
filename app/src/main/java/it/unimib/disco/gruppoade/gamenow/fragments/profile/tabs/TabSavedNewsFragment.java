package it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.SavedNewsListAdapter;
import it.unimib.disco.gruppoade.gamenow.models.NewsProvider;
import it.unimib.disco.gruppoade.gamenow.models.PieceOfNews;

public class TabSavedNewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SavedNewsListAdapter adapter;
    private List<PieceOfNews> mSavedNewsModelList;

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

        // Recupero il recyclerview dal layout xml
        mRecyclerView = root.findViewById(R.id.recyclerView);

        mSavedNewsModelList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new SavedNewsListAdapter(getActivity(), mSavedNewsModelList);
        mRecyclerView.setAdapter(adapter);

        // TODO: Carico notizie salvate (per ora locale:)
        mSavedNewsModelList.add(new PieceOfNews("News salvata 1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                "https://google.it/", LocalDateTime.now(),
                "https://www.callofduty.com/content/dam/atvi/callofduty/cod-touchui/kronos/common/social-share/social-share-image.jpg",
                "23544645764657845734563465",
                new NewsProvider("EuroGamer",
                        "https://www.eurogamer.it/",
                        "https://www.eurogamer.it/?format=rss&platform=PS4",
                        "PS4")));

        mSavedNewsModelList.add(new PieceOfNews("News salvata 2",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                "https://google.it/", LocalDateTime.now(),
                "https://www.callofduty.com/content/dam/atvi/callofduty/cod-touchui/kronos/common/social-share/social-share-image.jpg",
                "23544645764657845734563465",
                new NewsProvider("EuroGamer",
                        "https://www.eurogamer.it/",
                        "https://www.eurogamer.it/?format=rss&platform=PS4",
                        "XBOX")));

        mSavedNewsModelList.add(new PieceOfNews("News salvata 3",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                "https://google.it/", LocalDateTime.now(),
                "https://www.callofduty.com/content/dam/atvi/callofduty/cod-touchui/kronos/common/social-share/social-share-image.jpg",
                "23544645764657845734563465",
                new NewsProvider("EuroGamer",
                        "https://www.eurogamer.it/",
                        "https://www.eurogamer.it/?format=rss&platform=PS4",
                        "SWITCH")));

        // Carico notizie nel RecyclerView
        adapter.notifyDataSetChanged();

        return root;
    }
}