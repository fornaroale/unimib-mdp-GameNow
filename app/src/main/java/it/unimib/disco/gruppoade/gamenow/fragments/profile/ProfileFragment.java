package it.unimib.disco.gruppoade.gamenow.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.adapters.MyFragmentPagerAdapter;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        viewPager.setOffscreenPageLimit(2);

        setPagerAdapter();
        setTabLayout();
        setRetainInstance(true);

        return root;
    }

    private void setPagerAdapter() {
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Utente");
        tabLayout.getTabAt(1).setText("News");
    }
}
