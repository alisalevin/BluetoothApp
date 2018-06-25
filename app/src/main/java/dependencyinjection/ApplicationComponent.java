
package dependencyinjection;

import android.app.Application;

import com.ispresearch.bluetoothsensor.alldataentities.AllDataEntitiesFragment;
import com.ispresearch.bluetoothsensor.analysisgraph.AnalysisGraphFragment;
import com.ispresearch.bluetoothsensor.graph.GraphFragment;
import com.ispresearch.bluetoothsensor.main.MainFragment;
import com.ispresearch.bluetoothsensor.savedata.SaveDataFragment;

import javax.inject.Singleton;

import dagger.Component;


//Annotated as a Singleton since we don't want to have multiple instances of a Single Database

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {

    void inject(AllDataEntitiesFragment entitiesFragment);
    void inject(SaveDataFragment saveDataFragment);
    void inject(GraphFragment graphFragment);
    void inject(AnalysisGraphFragment analysisGraphFragment);
    void inject(MainFragment mainFragment);
    Application application();
}
