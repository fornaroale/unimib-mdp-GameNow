package it.unimib.disco.gruppoade.gamenow.ui.profile;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.gruppoade.gamenow.R;
import it.unimib.disco.gruppoade.gamenow.TagComparator;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    // inserisco variabili
    private List<String> tags = new ArrayList<String>();

    // comparator per stringhe

    //private String[] tags = {"PS4", "XBOX", "RPG", "FAnTasy", "FPS", null, "corsa", "Sparatutto In prima Persona"};
    private ChipGroup chipGroup;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // inizio codice

        // popolo lista tags
        popola(tags);

        // inserisco codice
        chipGroup = view.findViewById(R.id.chipGroup);

        if (tags != null)
            for (String text : tags) {

                // se il nome esiste
                if (text != null) {

                    text = formatText(text);

                    // creo la chip
                    Chip chip = creaChip(text, view);


                    // aggiunta chips alla chipsvgroup
                    chipGroup.addView(chip);
                    chipGroup.setVisibility(view.getVisibility());
                }
            }


        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void popola(List<String> tags) {
        //  popolo il tags

        tags.add("PS4");
        tags.add("XBOX");
        tags.add("RPG");
        tags.add("FAnTasy");
        tags.add("FPS");
        tags.add(null);
        tags.add("corsa");
        tags.add("Sparatutto In prima Persona");
        tags.add("Nintendo");

        // sorting
        TagComparator comparatore = new TagComparator();
        tags.sort(comparatore);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortedAdd(String element, List<String> tags) {

        // aggiungo l'elemento
        tags.add(element);

        // sorting
        TagComparator comparatore = new TagComparator();
        tags.sort(comparatore);
    }

    private String formatText(String text) {
        String result = text;

        // Formatto le stringhe con la prima lettera maiuscola e le successive in minuscolo
        result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
        return result;
    }

    private Chip creaChip(String text, View tempView) {

        // creo la chips e la setto
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        // creo delle variabili final
        // mi servono per poterle usare nell'azione del tasto UNDO
        final String tmpString = text;
        final View tmpView = tempView;
        final Chip tmpChip = chip;

        // associo alla x la rimozione
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rimuovo l'elemento dal chipGroup
                chipGroup.removeView(v);

                // in futuro andrà rimossa o da file o da Database
                //TODO rimuovo l'elemento dal vettore
                tags.remove(tmpString);


                // Creo la snackbar
                Snackbar mySnackbar = Snackbar.make(chipGroup, "Tag eliminato: " + tmpString, Snackbar.LENGTH_SHORT);

                // associo la funzione al tasto UNDO
                mySnackbar.setAction("Undo", new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {

                        // richiamo la stessa funzione (ricorsione(?))
                        creaChip(tmpString, tmpView);

                        // aggiungo nuovamente la chips al chipsGroup
                        chipGroup.addView(tmpChip);
                        chipGroup.setVisibility(tmpView.getVisibility());

                        // in futuro la rimozione avverà su file o database
                        //TODO riaggiungoi l'elemento dal vettore
                        sortedAdd(tmpString, tags);

                    }
                });
                // mostro la snackbar
                mySnackbar.show();


            }
        });

        // ritorno la chip creata
        return chip;
    }
}
