package com.tuchwords.wordstudy;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    final SavedStateHandle mSavedStateHandle;

    // Constructor injected with SavedStateHandle
    public MyViewModel(SavedStateHandle savedStateHandle) {
        mSavedStateHandle = savedStateHandle;
    }

    // onCleared() is called when the ViewModel is destroyed
    @Override
    protected void onCleared() {
        super.onCleared();
        // Cleanup resources here if needed (e.g., cancel network requests)
    }
}