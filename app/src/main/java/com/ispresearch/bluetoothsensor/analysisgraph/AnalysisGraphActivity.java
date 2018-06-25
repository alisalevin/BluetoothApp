package com.ispresearch.bluetoothsensor.analysisgraph;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.util.BasicActivity;

/**
 * Created by Alisa on 4/24/18.
 */

public class AnalysisGraphActivity extends BasicActivity {

    private static final String ANALYSIS_GRAPH_FRAG = "ANALYSIS_GRAPH_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        FragmentManager manager = getSupportFragmentManager();

        AnalysisGraphFragment fragment = (AnalysisGraphFragment) manager.findFragmentByTag(ANALYSIS_GRAPH_FRAG);

        if (fragment == null)
            fragment =  AnalysisGraphFragment.newInstance();

        //i added fragment util activity and this...still doesn't work
        addFragmentToActivity(manager, fragment, R.id.root__analysis_graph_activity, ANALYSIS_GRAPH_FRAG);

    }
}
