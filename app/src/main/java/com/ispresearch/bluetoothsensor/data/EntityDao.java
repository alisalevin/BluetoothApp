package com.ispresearch.bluetoothsensor.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Alisa on 4/8/18.
 */

@Dao
public interface EntityDao {

    @Query("SELECT * FROM DataEntity")
    LiveData<List<DataEntity>> getAllDataEntities();

    @Query("SELECT * FROM DataEntity WHERE entity_id * :uid")
    LiveData<DataEntity> getDataEntityById(String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataEntity(DataEntity dataEntity);

    @Delete()
    void deleteDataEntity(DataEntity dataEntity);

    @Query("DELETE FROM DataEntity")
    void deleteTable();
}
