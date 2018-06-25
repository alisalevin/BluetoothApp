package dependencyinjection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final com.ispresearch.bluetoothsensor.BluetoothSensor  application;
    public ApplicationModule(com.ispresearch.bluetoothsensor.BluetoothSensor application) {
        this.application = application;
    }

    @Provides
    com.ispresearch.bluetoothsensor.BluetoothSensor provideBluetoothSensorApplication(){
        return application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }
}
