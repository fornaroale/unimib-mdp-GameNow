package it.unimib.disco.gruppoade.gamenow.fragments.comingsoon;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.IncomingAdapter;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.ApiClient;
import it.unimib.disco.gruppoade.gamenow.fragments.comingsoon.utils.Constants;
import it.unimib.disco.gruppoade.gamenow.models.Game;
import it.unimib.disco.gruppoade.gamenow.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private int totalItemCount, lastVisibleItem, visibleItemCount, threshold = 1;

    private ValueEventListener postListenerFirstUserData;

    private ValueEventListener postListenerUserData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_comingsoon, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comingSoonViewModel = new ViewModelProvider(requireActivity()).get(ComingSoonViewModel.class);
        resetBody();

        postListenerFirstUserData = new ValueEventListener() {
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
                        boolean conditions = totalItemCount == visibleItemCount ||(totalItemCount <= (lastVisibleItem + threshold) && dy > 0 && !comingSoonViewModel.isLoading());


                        if (conditions) {
                            List<Game> gameList = new ArrayList<>();
                            boolean conditions2 = gamesLiveData.getValue() != null &&
                                    gamesLiveData.getValue().get(gamesLiveData.getValue().size() -1) != null;
                            if (conditions2) {
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
                                comingSoonViewModel.setLoading(true);
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
                        if(comingSoonViewModel.isLoading()) {
                            comingSoonViewModel.setLoading(false);
                            comingSoonViewModel.setCurrentResults(games.size());
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

        postListenerUserData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                chooseButton(ps4Btn.getId());
            }
        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                chooseButton(allBtn.getId());
            }
        });

        xboxBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi( api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                chooseButton(xboxBtn.getId());
            }
        });

        pcBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                chooseButton(pcBtn.getId());
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                chooseButton(switchBtn.getId());
            }
        });


    }

    private List<Game> getGameList(String body){
        LiveData<List<Game>> gameList = comingSoonViewModel.getGames(body);
        if (gameList != null)
            return gameList.getValue();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void chooseButton(int buttonId){
        lottieAnimationView.setVisibility(View.VISIBLE);
        comingSoonViewModel.setOffset(0);
        gamesLiveData.postValue(null);
        switch (buttonId){
            case R.id.button_ps4:
                ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                allBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                xboxBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                pcBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                switchBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));

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
                xboxBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                allBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                pcBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                switchBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));


                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {49}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            case R.id.button_pc:
                pcBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                xboxBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                allBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                switchBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));

                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {6}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            case R.id.button_switch:
                switchBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                xboxBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                pcBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                allBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));

                bodystart = "fields name,cover.url,platforms.abbreviation,first_release_date,summary,storyline,total_rating, videos.video_id;\n" +
                        "where category = 0 & platforms= {130}& first_release_date > "+ todayInSecs +";\n";
                bodyOffset = "offset 0;\n";
                bodyEnd = "sort first_release_date asc;\nlimit " + Constants.PAGE_SIZE + ";\n";

                body = bodystart + bodyOffset + bodyEnd;

                gamesList = comingSoonViewModel.changeConsole(body);
                incomingAdapter.setData(gamesList.getValue());
                break;
            default:
                allBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                //reset other btn colors to off
                ps4Btn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                xboxBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                pcBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));
                switchBtn.setBackgroundTintList(getResources().getColorStateList(R.color.buttonBackground));


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