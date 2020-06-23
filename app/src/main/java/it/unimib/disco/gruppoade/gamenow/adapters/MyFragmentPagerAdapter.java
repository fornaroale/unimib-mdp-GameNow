package it.unimib.disco.gruppoade.gamenow.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs.TabSavedGamesFragment;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs.TabSavedNewsFragment;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs.TabSettingsFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabSavedNewsFragment();
            case 1:
                return new TabSavedGamesFragment();
            case 2:
                return new TabSettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
