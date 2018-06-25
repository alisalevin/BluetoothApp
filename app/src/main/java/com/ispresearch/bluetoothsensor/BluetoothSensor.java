package com.ispresearch.bluetoothsensor;

import android.app.Application;

import dependencyinjection.ApplicationComponent;
import dependencyinjection.ApplicationModule;
import dependencyinjection.DaggerApplicationComponent;
import dependencyinjection.RoomModule;

public class BluetoothSensor extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .roomModule(new RoomModule(this))
                .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}