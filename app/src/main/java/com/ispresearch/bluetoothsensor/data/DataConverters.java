package com.ispresearch.bluetoothsensor.data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Alisa on 4/13/18.
 */

public class DataConverters {
    @TypeConverter
    public static String arrayToJson(double[] array) {
        Gson gson = new Gson();
        String json = gson.toJson(array);
        return json;
    }

    @TypeConverter
    public static double[] jsonToArrray(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<double[]>() {}.getType();
        double[] array = gson.fromJson(json, type);
        return array;

    }
}
