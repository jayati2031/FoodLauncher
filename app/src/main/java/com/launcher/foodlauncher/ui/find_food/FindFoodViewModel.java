package com.launcher.foodlauncher.ui.find_food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindFoodViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FindFoodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}