package it.unimib.disco.gruppoade.gamenow.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.ChipGroup;

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
                /*Chip chip = (Chip) inflater.inflate(R.layout.chip_item, container, false);
                chip.setText(text.toUpperCase());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Chip tp_chip = (Chip) v;
                        String tagDaEliminare = (String) tp_chip.getText();
                        chipGroup.removeView(v);
                */
                        // TODO Leggere da file il tag eliminato ed eliminarlo
                  /*  try {
                        fileReader.eliminaTag(tagDaEliminare);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


                    }
        // });

                //add to group
        //chipGroup.addView(chip);
        //   }


        return view;
    }
}
