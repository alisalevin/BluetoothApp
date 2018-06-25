package com.ispresearch.bluetoothsensor.analysisgraph;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.ispresearch.bluetoothsensor.BluetoothSensor;
import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.graph.GraphActivity;

import java.util.ArrayList;
import java.util.Arrays;



public class AnalysisGraphFragment  extends Fragment {

    private View v;
    private Button switchGraph;
    private Button mReturn;
    private double x = 0.0;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    PointsGraphSeries<DataPoint> pointsS;
    PointsGraphSeries<DataPoint> pointsD;
    private double[] data;
    int length;
    private ArrayList<Integer> systole = new ArrayList<>();
    private ArrayList<Integer> diastole = new ArrayList<>();

    private int fs = 200;


    public AnalysisGraphFragment() {}

    public static AnalysisGraphFragment newInstance() { return new AnalysisGraphFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BluetoothSensor) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        Bundle bundle = getActivity().getIntent().getExtras();

        data = bundle.getDoubleArray("data");
        length = bundle.getInt("length");

        double threshold = 1.5;
        int sample = (200/3);

        //initialize potential max value array
        ArrayList<Integer> potential = new ArrayList<Integer>();

        //finds all potential max values above threshold in file
        for (int i = 0;i < length; i = i + sample) {
            double[] temp = Arrays.copyOfRange(data, i, i+sample);

            int maxIndex = 0;
            double maxVal = temp[0];
            for (int ktr = 0; ktr < temp.length; ktr++) {
                if (temp[ktr] > maxVal) {
                    maxVal = temp[ktr];
                    maxIndex = ktr;
                }
            }
            int actualIndex = maxIndex+i;
            double actualVal = (data[actualIndex]);

            if (actualVal>threshold) {
                potential.add(actualIndex);

            }
        }

        ArrayList<Integer> noduplicate = new ArrayList<Integer>(); //this array has the s and d index values


        //filters out max values closer than .2 seconds together
        double timethreshold = fs*.3;
        int duplicate = 0;

        int plength = potential.size();

        for (int k = 0; k<plength; k++){

            for (int j = 1; j<plength; j++){
                duplicate = 0;
                if ((potential.get(j)-potential.get(k)) < timethreshold){
                    duplicate = 1;
                }
            }
            if (duplicate == 0) {
                noduplicate.add(potential.get(k));


            }

        }

        int first = 1;
        int last = 0;
        int prevval = -1;
        double prevt = 0;
        double currentt = 0;
        double nextt = 0;

        for (int i = 0; i < noduplicate.size(); i++) {
            if (i == 1) {
                first = 0;
            }
            //Base case for last peak in array (because there's nothing to compare it to)
            if (i == noduplicate.size()-1) {
                last = 1;
                if (prevval == 1) {
                    systole.add(noduplicate.get(i));
                }
                else {
                    diastole.add(noduplicate.get(i));
                }
            }

            if (first == 0 && last == 0) {
                prevt = noduplicate.get(i-1);
                currentt = noduplicate.get(i);
                nextt = noduplicate.get(i+1);

                if (currentt-prevt > nextt-currentt) {
                    systole.add(noduplicate.get(i));
                    prevval = 0;

                    if (i == 1) {
                        diastole.add(noduplicate.get(i - 1));
                    }
                }
                else {
                    diastole.add(noduplicate.get(i));
                    prevval = 1;

                    if (i == 1) {
                        systole.add(noduplicate.get(i - 1));
                    }
                }

            }
        }

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
//        mReturn = v.findViewById(R.id.returnMain);

        switchGraph.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), GraphActivity.class);
                intent.putExtra("length", length);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        }));



        graph = v.findViewById(R.id.graph);

        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < length; i++) {
            series.appendData(new DataPoint(x, data[i]), true, length);
            x = x + 0.005; //0.000166667 for 6kz //.00005 for 2kz //.005 for 200 Hz
        }


        pointsS = new PointsGraphSeries<DataPoint>();
        pointsD = new PointsGraphSeries<DataPoint>();
        int length1 = systole.size();
        int length2 = diastole.size();
        double height1 = 0;
        double height2 = 0;
        for (int i = 0; i < length1; i++) {
            pointsS.appendData(new DataPoint(Double.valueOf((systole.get(i)))/200, height1), true, length1);
        }
        for (int i = 0; i < length2; i++) {
            pointsD.appendData(new DataPoint(Double.valueOf(diastole.get(i))/200, height2), true, length2);
        }

        graph.addSeries(series);
        graph.addSeries(pointsS);
        graph.addSeries(pointsD);

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

        pointsS.setColor(Color.GREEN);

        pointsD.setColor(Color.RED);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            pointsS.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(5);
                    canvas.drawLine(x, y - 180, x, y + 180, paint);
                    paint.setTextSize(35);
                    canvas.drawText("s", x, y-185, paint);
                }
            });

            pointsD.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(5);
                    canvas.drawLine(x, y-180, x, y+180, paint);
                    paint.setTextSize(35);
                    canvas.drawText("d", x, y-185, paint);
                }
            });

        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            pointsS.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(5);
                    canvas.drawLine(x, y-350, x, y+350, paint);
                    paint.setTextSize(45);
                    canvas.drawText("s", x, y-358, paint);
                }
            });

            pointsD.setCustomShape(new PointsGraphSeries.CustomShape() {
                @Override
                public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                    paint.setStrokeWidth(5);
                    canvas.drawLine(x, y-350, x, y+350, paint);
                    paint.setTextSize(45);
                    canvas.drawText("d", x, y-358, paint);
                }
            });
        }




        return v;
    }

}






