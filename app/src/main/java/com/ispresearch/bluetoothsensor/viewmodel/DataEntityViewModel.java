package com.ispresearch.bluetoothsensor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.ispresearch.bluetoothsensor.data.DataEntity;
import com.ispresearch.bluetoothsensor.data.DataEntityRepository;

/**
 * Created by Alisa on 4/13/18.
 */

public class DataEntityViewModel extends ViewModel {

    private DataEntityRepository repository;

    DataEntityViewModel(DataEntityRepository repository) {
        this.repository = repository;
    }

    public LiveData<DataEntity> getEntityById(String itemId) {
        return repository.getDataEntity(itemId);
    }
}
