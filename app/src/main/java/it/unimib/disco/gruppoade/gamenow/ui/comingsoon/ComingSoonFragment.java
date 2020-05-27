package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import it.unimib.disco.gruppoade.gamenow.R;

public class ComingSoonFragment extends Fragment {

    private ComingSoonViewModel comingSoonViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        comingSoonViewModel =
                ViewModelProviders.of(this).get(ComingSoonViewModel.class);
        View root = inflater.inflate(R.layout.fragment_comingsoon, container, false);
        final TextView textView = root.findViewById(R.id.text_comingsoon);
        comingSoonViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
