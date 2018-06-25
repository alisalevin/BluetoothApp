package com.ispresearch.bluetoothsensor.data;

import android.arch.persistence.room.*;

/**
 * Created by Alisa on 4/8/18.
 */

@Database(entities = {DataEntity.class}, version = 1, exportSchema = false)
@android.arch.persistence.room.TypeConverters(DataConverters.class)
public abstract class DataEntityDatabase extends RoomDatabase {

    public abstract EntityDao entityDao();
}
