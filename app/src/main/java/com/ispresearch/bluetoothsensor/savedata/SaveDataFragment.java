package com.ispresearch.bluetoothsensor.savedata;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import com.ispresearch.bluetoothsensor.BluetoothSensor;
import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.alldataentities.AllDataEntitiesActivity;
import com.ispresearch.bluetoothsensor.data.DataEntity;
import com.ispresearch.bluetoothsensor.viewmodel.NewDataEntityViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Alisa on 4/9/18.
 */

public class SaveDataFragment extends Fragment {

    private Button mDone;
    private EditText mName;
    private String name;
    private double[] mData;
    private View v;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private NewDataEntityViewModel newDataEntityViewModel;

    public static SaveDataFragment newInstance() {
        return new SaveDataFragment();
    }

    public SaveDataFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BluetoothSensor) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Set up and subscribe (observe) to the ViewModel
        newDataEntityViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(NewDataEntityViewModel.class);

        Bundle bundle = getActivity().getIntent().getExtras();
        final double[] data = bundle.getDoubleArray("data");
        int length = bundle.getInt("length");

        mData = data;

        mDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                name = mName.getText().toString();
                DataEntity dataEntity = new DataEntity(getDateId(), mData, name);
                newDataEntityViewModel.addNewDataEntityToDatabase(dataEntity);
                Intent intent = new Intent(getActivity(), AllDataEntitiesActivity.class);
                startActivity(intent);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_save_data, container, false);

        mDone = v.findViewById(R.id.done);
        mName = v.findViewById(R.id.name);

        return v;
    }
    @NonNull
    private String getDateId() {
        Date currentDate = Calendar.getInstance().getTime();

        DateFormat format = new SimpleDateFormat("yyy/MM/dd/kk:mm:ss", Locale.US);

        return format.format(currentDate);
    }

}
