package it.unimib.disco.gruppoade.gamenow.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.MyFragmentPagerAdapter;

public class ProfileFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        viewPager.setOffscreenPageLimit(3);

        setPagerAdapter();
        setTabLayout();
        setRetainInstance(true);

        return root;
    }

    private void setPagerAdapter() {
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.tab_news_title);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.tab_games_title);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(R.string.tab_user_title);
    }

}
