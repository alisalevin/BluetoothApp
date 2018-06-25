package com.ispresearch.bluetoothsensor.alldataentities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.ispresearch.bluetoothsensor.util.BasicActivity;
import com.ispresearch.bluetoothsensor.R;

public class AllDataEntitiesActivity extends BasicActivity {

    private static final String DATA_FRAG = "DATA_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_entities);

        FragmentManager manager = getSupportFragmentManager();

        AllDataEntitiesFragment fragment = (AllDataEntitiesFragment) manager.findFragmentByTag(DATA_FRAG);

        if (fragment == null)
            fragment = AllDataEntitiesFragment.newInstance();

        //i added fragment util activity and this...still doesn't work
        addFragmentToActivity(manager, fragment, R.id.root_activity_all_entities, DATA_FRAG);

    }
}
