package com.ispresearch.bluetoothsensor.data;


import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Alisa on 4/8/18.
 */

public class DataEntityRepository {
    private final EntityDao entityDao;


    @Inject
    public DataEntityRepository(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public LiveData<DataEntity> getDataEntity(String uid) {
        return entityDao.getDataEntityById(uid);
    }
    public LiveData<List<DataEntity>> getListOfDataEntities() {
        return entityDao.getAllDataEntities();
    }
    public void deleteDataEntity(DataEntity dataEntity) {
        entityDao.deleteDataEntity(dataEntity);
    }
    public void insertEntity(DataEntity dataEntity) {
        entityDao.insertDataEntity(dataEntity);
    }

    public void deleteTable() {
        entityDao.deleteTable();
    }
}
