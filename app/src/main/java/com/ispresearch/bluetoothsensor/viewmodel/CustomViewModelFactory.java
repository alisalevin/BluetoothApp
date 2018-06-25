package com.ispresearch.bluetoothsensor.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ispresearch.bluetoothsensor.data.DataEntityRepository;

import javax.inject.Inject;

/**
 * Created by Alisa on 4/13/18.
 */

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private final DataEntityRepository repository;
    @Inject
    public CustomViewModelFactory(DataEntityRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(DataEntityCollectionViewModel.class))
            return (T) new DataEntityCollectionViewModel(repository);

        else if (aClass.isAssignableFrom(NewDataEntityViewModel.class))
            return (T) new NewDataEntityViewModel(repository);

        else if(aClass.isAssignableFrom(DataEntityViewModel.class))
            return (T) new DataEntityViewModel(repository);

        else
            throw new IllegalArgumentException("ViewModel Not Found");
    }
}
