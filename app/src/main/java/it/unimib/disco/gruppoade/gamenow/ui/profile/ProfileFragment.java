package it.unimib.disco.gruppoade.gamenow.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import it.unimib.disco.gruppoade.gamenow.PopActivity;
import it.unimib.disco.gruppoade.gamenow.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    // inserisco variabili
    private String[] tags = {"PS4", "XBOX", "RPG", "FAntasy", "FPS", "corsa", "Sparatutto In prima Persona"};
    private ChipGroup chipGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // inserisco codice
        chipGroup = view.findViewById(R.id.chipGroup);


        if (tags != null)
            for (String text : tags) {
                // creo la chips
                Chip chip = new Chip(getContext());
                chip.setText(text);
                chip.setCloseIconVisible(true);
                chip.setCheckable(false);
                chip.setClickable(false);

                // associo alla x la rimozione
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lancio la activity pop-up
                        Intent i = new Intent(getContext(), PopActivity.class);
                        startActivity(i);

                        // leggo la risposta
                        //TODO leggere risposta della schermata pop-up

                        // se si rimuovo l'elemento
                        //chipGroup.removeView(v);
                    }
                });


                // aggiunta chips alla chipsvgroup
                chipGroup.addView(chip);
                chipGroup.setVisibility(view.getVisibility());


            }


        return view;
    }
}
