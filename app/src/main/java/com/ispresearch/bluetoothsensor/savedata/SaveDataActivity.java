package com.ispresearch.bluetoothsensor.savedata;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.ispresearch.bluetoothsensor.util.BasicActivity;
import com.ispresearch.bluetoothsensor.R;
/**
 * Created by Alisa on 4/22/18.
 */

public class SaveDataActivity extends BasicActivity {
    private static final String SAVE_DATA_FRAG = "SAVE_DATA_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data); //what should this be?


        FragmentManager manager = getSupportFragmentManager();

        SaveDataFragment fragment = (SaveDataFragment) manager.findFragmentByTag(SAVE_DATA_FRAG);

        if (fragment == null) {
            fragment = SaveDataFragment.newInstance();
        }

        addFragmentToActivity(manager, fragment, R.id.root_activity_save_data, SAVE_DATA_FRAG);

    }
}
