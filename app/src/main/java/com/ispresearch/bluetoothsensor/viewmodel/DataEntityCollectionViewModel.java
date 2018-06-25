package com.ispresearch.bluetoothsensor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.ispresearch.bluetoothsensor.data.DataEntity;
import com.ispresearch.bluetoothsensor.data.DataEntityRepository;

import java.util.List;

/**
 * Created by Alisa on 4/13/18.
 */

public class DataEntityCollectionViewModel extends ViewModel {

    private DataEntityRepository repository;

    DataEntityCollectionViewModel (DataEntityRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<DataEntity>> getDataEntities() {
        return repository.getListOfDataEntities();
    }

    //deletes all data for testing purposes
    public void deleteDataEntities() {
        repository.deleteTable();
    }

    public void deleteDataEntity(DataEntity dataEntity) {
        DeleteItemTask deleteItemTask = new DeleteItemTask();
        deleteItemTask.execute(dataEntity);
    }

    private class DeleteItemTask extends AsyncTask<DataEntity, Void, Void> {

        @Override
        protected Void doInBackground(DataEntity... item) {
            repository.deleteDataEntity(item[0]);
            return null;
        }
    }
}
