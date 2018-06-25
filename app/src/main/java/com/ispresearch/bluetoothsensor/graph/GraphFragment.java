package com.ispresearch.bluetoothsensor.graph;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ispresearch.bluetoothsensor.analysisgraph.AnalysisGraphActivity;
import com.ispresearch.bluetoothsensor.BluetoothSensor;
import com.ispresearch.bluetoothsensor.R;

import javax.inject.Inject;

public class GraphFragment extends Fragment {

    private Button switchGraph;
//    private Button mReturn;
    private double x = 0.0;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private double[] data;
    int length;
    private View v;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public GraphFragment() {}


    public static GraphFragment newInstance() {
        return new GraphFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BluetoothSensor) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        Bundle bundle = getActivity().getIntent().getExtras();

        data = bundle.getDoubleArray("data");
        length = bundle.getInt("length");

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_graph, container, false);


        switchGraph = v.findViewById(R.id.switchGraph);
        switchGraph.setText("Graph Toggle");

        switchGraph.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), AnalysisGraphActivity.class);
                intent.putExtra("length", length);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        }));

//        mReturn.setOnClickListener((new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra("data", data);
//                startActivity(intent);
//            }
//        }));

        graph = v.findViewById(R.id.graph);

        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < length; i++) {
            series.appendData(new DataPoint(x, data[i]), true, length);
            x = x + 0.005; //0.000166667 for 6kz //.00005 for 2kz //0.005 for 200 Hz
        }
        graph.addSeries(series);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(.001);
        graph.getViewport().setMaxX(1);


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-6);
        graph.getViewport().setMaxY(6);

//        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScrollableY(true);

        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
//        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        series.setColor(Color.BLUE);
//        series.setDrawDataPoints(true);
//        series.setDataPointsRadius(4);
//        series.setThickness(3);


        return v;
    }




}
