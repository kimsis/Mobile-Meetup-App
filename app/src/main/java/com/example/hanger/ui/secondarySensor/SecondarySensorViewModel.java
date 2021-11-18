package com.example.hanger.ui.secondarySensor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SecondarySensorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SecondarySensorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is secondary sensor fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}