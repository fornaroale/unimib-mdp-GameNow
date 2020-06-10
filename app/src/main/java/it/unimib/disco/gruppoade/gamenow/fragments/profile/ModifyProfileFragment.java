package it.unimib.disco.gruppoade.gamenow.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.unimib.disco.gruppoade.gamenow.R;

public class ModifyProfileFragment extends Fragment {


    private Button salvaModifiche;

    public ModifyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify_profile, container, false);




    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        salvaModifiche = getView().findViewById(R.id.btn_salva_modifiche);
        salvaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                CardView cv_info = getView().findViewById(R.id.cv_infoaccount);
//                CardView cv_logout = getView().findViewById(R.id.cv_logout);
//                CardView cv_elimina = getView().findViewById(R.id.cv_deleteaccount);
//                CardView cv_topics = getView().findViewById(R.id.cv_topics);
//
//                cv_info.setVisibility(View.VISIBLE);
//                cv_logout.setVisibility(View.VISIBLE);
//                cv_elimina.setVisibility(View.VISIBLE);
//                cv_topics.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}