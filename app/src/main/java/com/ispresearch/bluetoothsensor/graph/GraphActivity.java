package com.ispresearch.bluetoothsensor.graph;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.util.BasicActivity;

/**
 * Created by Alisa on 4/23/18.
 */

public class GraphActivity extends BasicActivity {

    private static final String GRAPH_FRAG = "GRAPH_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        FragmentManager manager = getSupportFragmentManager();

       GraphFragment fragment = (GraphFragment) manager.findFragmentByTag(GRAPH_FRAG);

        if (fragment == null)
            fragment = GraphFragment.newInstance();

        //i added fragment util activity and this...still doesn't work
        addFragmentToActivity(manager, fragment, R.id.root_graph_activity, GRAPH_FRAG);

    }
}
