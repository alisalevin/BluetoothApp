package com.ispresearch.bluetoothsensor.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.util.BasicActivity;

/**
 * Created by Alisa on 4/25/18.
 */

public class MainActivity extends BasicActivity {

    private static final String MAIN_FRAG = "MAIN_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();

       MainFragment fragment = (MainFragment) manager.findFragmentByTag(MAIN_FRAG);

        if (fragment == null)
            fragment = MainFragment.newInstance();

        //i added fragment util activity and this...still doesn't work
        addFragmentToActivity(manager, fragment, R.id.root_main_activity, MAIN_FRAG);

    }
}
