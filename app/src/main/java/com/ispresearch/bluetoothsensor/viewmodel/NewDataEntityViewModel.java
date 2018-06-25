package com.ispresearch.bluetoothsensor.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.ispresearch.bluetoothsensor.data.DataEntity;
import com.ispresearch.bluetoothsensor.data.DataEntityRepository;

/**
 * Created by Alisa on 4/13/18.
 */

public class NewDataEntityViewModel extends ViewModel {

    private DataEntityRepository repository;

    NewDataEntityViewModel(DataEntityRepository repository) {
        this.repository = repository;
    }

    //Attach LiveData to the Database
    public void addNewDataEntityToDatabase(DataEntity entity) {
        new AddItemTask().execute(entity);
    }

    private class AddItemTask extends AsyncTask<DataEntity, Void, Void> {
        @Override
        protected Void doInBackground(DataEntity... item) {
            repository.insertEntity(item[0]);
            return null;
        }
    }
}
