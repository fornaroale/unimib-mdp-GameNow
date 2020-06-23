package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.Constants;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.User;

import static android.view.View.GONE;

public class ComingSoonFragment extends Fragment {

    private static final String TAG = "ComingSoonFragment";
    private ComingSoonViewModel comingSoonViewModel;

    private long todayInSecs = (new Date().getTime()/1000);

    private LottieAnimationView lottieAnimationView;
    private String body, bodystart, bodyOffset, bodyEnd;
    private ImageButton ps4Btn, xboxBtn, pcBtn, switchBtn;
    private Button allBtn;
    private RecyclerView recyclerView;
    private Observer<List<Game>> observer;
    private LiveData<List<Game>> gamesList;
    private MutableLiveData<List<Game>> gamesLiveData;
    private IncomingAdapter incomingAdapter;
    private User user;
    private boolean pc, ps4, xbox, nSwitch, all;

    private int totalItemCount, lastVisibleItem, visibleItemCount, threshold = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comingsoon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comingSoonViewModel = new ViewModelProvider(requireActivity()).get(ComingSoonViewModel.class);

        resetBody();

        ValueEventListener postListenerFirstUserData = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                incomingAdapter = new IncomingAdapter(getActivity(), getGameList(body), new IncomingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Game game) {
                        ComingSoonFragmentDirections.DisplayGameInfo action = ComingSoonFragmentDirections.displayGameInfo(game);
                        Navigation.findNavController(view).navigate(action);
                    }
                }, user);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(incomingAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                        visibleItemCount = layoutManager.getChildCount();
                        boolean conditions = totalItemCount == visibleItemCount ||
                                (totalItemCount <= (lastVisibleItem + threshold)
                                        && dy > 0
                                        && !comingSoonViewModel.isLoading())
                                        && comingSoonViewModel.getmGamesLiveData().getValue() != null;

                        if (conditions) {
                            List<Game> gameList = new ArrayList<>();
                            boolean conditions2 = gamesLiveData.getValue() != null;
                            if (conditions2) {
                                comingSoonViewModel.setLoading(true);
                                List<Game> currentList = gamesLiveData.getValue();
                                currentList.add(null);
                                gameList.addAll(currentList);
                                gamesLiveData.postValue(gameList);
                                int currentOffset = comingSoonViewModel.getOffset();
                                comingSoonViewModel.setOffset(currentOffset + Constants.PAGE_SIZE);
                                bodyOffset = "offset " + comingSoonViewModel.getOffset() + ";\n";
                                body = bodystart + bodyOffset + bodyEnd;
                                Log.d(TAG, "onScrolled: Body " + body);
                                comingSoonViewModel.getMoreGames(body);
                                Constants.loadingSentinel = true;

                            }
                        }
                    }
                });

                observer = new Observer<List<Game>>() {
                    @Override
                    public void onChanged(List<Game> games) {
                        Log.d(TAG, "initRecyclerView: Init RecyclerView");
                        incomingAdapter.setData(games);
                        lottieAnimationView.setVisibility(GONE);
                        if (!Constants.loadingSentinel) {
                            comingSoonViewModel.setLoading(false);
                        }
                    }
                };

                gamesList = comingSoonViewModel.getGames(body);
                gamesList.observe(getViewLifecycleOwner(), observer);
                gamesLiveData = comingSoonViewModel.getmGamesLiveData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        ValueEventListener postListenerUserData = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
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
        allBtn = view.findViewById(R.id.button_all);
        ps4Btn = view.findViewById(R.id.button_ps4);
        xboxBtn = view.findViewById(R.id.button_xbox);
        pcBtn = view.findViewById(R.id.button_pc);
        switchBtn = view.findViewById(R.id.button_switch);
        recyclerView = view.findViewById(R.id.recyclerview);
        lottieAnimationView = view.findViewById(R.id.animation_view);



        //Buttons Listeners
        ps4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseButton(ps4Btn.getId()); }
        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseButton(allBtn.getId()); }
        });

        xboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseButton(xboxBtn.getId());
            }
        });

        pcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseButton(pcBtn.getId());
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseButton(switchBtn.getId());
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if(ps4)
            chooseButton(ps4Btn.getId());
        if(xbox)
            chooseButton(xboxBtn.getId());
        if(pc)
            chooseButton(pcBtn.getId());
        if(nSwitch)
            chooseButton(switchBtn.getId());
        if(all)
            chooseButton(allBtn.getId());
    }

    private List<Game> getGameList(String body){
        LiveData<List<Game>> gameList = comingSoonViewModel.getGames(body);
        if (gameList != null)
            return gameList.getValue();
        return null;
    }

    private void chooseButton(int buttonId){
        lottieAnimationView.setVisibility(View.VISIBLE);
        comingSoonViewModel.setOffset(0);
        gamesLiveData.postValue(null);
        PorterDuffColorFilter accentFilter = new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        PorterDuffColorFilter offFilter = new PorterDuffColorFilter(getResources().getColor(R.color.buttonBackground), PorterDuff.Mode.MULTIPLY);
        switch (buttonId){
            case R.id.button_ps4:
                ps4 = true;
                all = false;
                xbox = false;
                nSwitch = false;
                pc = false;


                ps4Btn.getBackground().setColorFilter(accentFilter);
                //ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                allBtn.setBackgroundColor(0xFFE1E2E1);
                xboxBtn.getBackground().setColorFilter(offFilter);
                pcBtn.getBackground().setColorFilter(offFilter);
                switchBtn.getBackground().setColorFilter(offFilter);

                comingSoonViewModel.setOffset(0);

                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {48}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;
                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;

            case R.id.button_xbox:
                ps4 = false;
                all = false;
                xbox = true;
                nSwitch = false;
                pc = false;
                xboxBtn.getBackground().setColorFilter(accentFilter);

                //reset other btn colors to off
                ps4Btn.getBackground().setColorFilter(offFilter);
                allBtn.setBackgroundColor(0xFFE1E2E1);
                pcBtn.getBackground().setColorFilter(offFilter);
                switchBtn.getBackground().setColorFilter(offFilter);


                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {49}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            case R.id.button_pc:
                ps4 = false;
                all = false;
                xbox = false;
                nSwitch = false;
                pc = true;

                pcBtn.getBackground().setColorFilter(accentFilter);

                //reset other btn colors to off
                ps4Btn.getBackground().setColorFilter(offFilter);
                xboxBtn.getBackground().setColorFilter(offFilter);
                allBtn.setBackgroundColor(0xFFE1E2E1);
                switchBtn.getBackground().setColorFilter(offFilter);

                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {6}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            case R.id.button_switch:
                ps4 = false;
                all = false;
                xbox = false;
                nSwitch = true;
                pc = false;

                switchBtn.getBackground().setColorFilter(accentFilter);

                //reset other btn colors to off
                ps4Btn.getBackground().setColorFilter(offFilter);
                xboxBtn.getBackground().setColorFilter(offFilter);
                pcBtn.getBackground().setColorFilter(offFilter);
                allBtn.setBackgroundColor(0xFFE1E2E1);

                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {130}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            default:
                ps4 = false;
                xbox = false;
                nSwitch = false;
                pc = false;
                all = true;

                allBtn.setBackgroundColor(0xFFFFBD45);

                //reset other btn colors to off
                ps4Btn.getBackground().setColorFilter(offFilter);
                xboxBtn.getBackground().setColorFilter(offFilter);
                pcBtn.getBackground().setColorFilter(offFilter);
                switchBtn.getBackground().setColorFilter(offFilter);


                resetBody();
                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
        }
    }


    private void resetBody(){
        bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                "where category = 0 & platforms= (130,49,48,6) & first_release_date > "+ todayInSecs +";\n";
        bodyOffset = "offset 0;\n";
        bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";
        body = bodystart + bodyOffset + bodyEnd;
    }


}