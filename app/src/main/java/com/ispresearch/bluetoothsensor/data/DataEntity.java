package com.ispresearch.bluetoothsensor.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

/**
 * Created by Alisa on 4/8/18.
 */

@Entity
public class DataEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo (name = "entity_id")
    private String uid;

    @TypeConverters(value = DataConverters.class)
    @ColumnInfo(name = "double_array")
    private double[] data;

    @ColumnInfo(name = "name")
    private String name;

    public DataEntity(String uid, double[] data, String name) {
        this.data = data;
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
