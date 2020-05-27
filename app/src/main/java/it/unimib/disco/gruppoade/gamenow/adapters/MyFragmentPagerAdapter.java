package it.unimib.disco.gruppoade.gamenow.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs.TabSavedNewsFragment;
import it.unimib.disco.gruppoade.gamenow.fragments.profile.tabs.TabSettingsFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabSettingsFragment();
            case 1:
                return new TabSavedNewsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
