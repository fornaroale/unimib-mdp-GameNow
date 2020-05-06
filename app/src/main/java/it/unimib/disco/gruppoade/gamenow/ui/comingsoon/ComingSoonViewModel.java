package it.unimib.disco.gruppoade.gamenow.ui.comingsoon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComingSoonViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ComingSoonViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is coming-soon fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}