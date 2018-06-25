package com.ispresearch.bluetoothsensor.alldataentities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ispresearch.bluetoothsensor.BluetoothSensor;
import com.ispresearch.bluetoothsensor.graph.GraphActivity;
import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.data.DataEntity;
import com.ispresearch.bluetoothsensor.main.MainActivity;
import com.ispresearch.bluetoothsensor.viewmodel.DataEntityCollectionViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Alisa on 4/13/18.
 */

public class AllDataEntitiesFragment extends Fragment {
    private static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    private List<DataEntity> entities;

    private Button mReturnHome;
    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private View v;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    DataEntityCollectionViewModel dataEntityCollectionViewModel;

    public AllDataEntitiesFragment() {}

    public static AllDataEntitiesFragment newInstance() {
        return new AllDataEntitiesFragment();
    }

    /*------------------------------- Lifecycle -------------------------------*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BluetoothSensor) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataEntityCollectionViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(DataEntityCollectionViewModel.class);
        dataEntityCollectionViewModel.getDataEntities().observe(this, new Observer<List<DataEntity>>() {
            @Override
            public void onChanged(@Nullable List<DataEntity> entities) {
                if (AllDataEntitiesFragment.this.entities == null) {
                    setListData(entities);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_entities, container, false);
        mReturnHome = v.findViewById(R.id.returnHome);

        recyclerView = v.findViewById(R.id.rec_list_activity);
        layoutInflater = getActivity().getLayoutInflater();

        mReturnHome.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // initializes the data in the list
    public void setListData(List<DataEntity> entities) {
        this.entities = entities;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);


        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );

        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_black));
        recyclerView.addItemDecoration(
                itemDecoration
        );


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    /*-------------------- RecyclerView Boilerplate ----------------------*/

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        @Override
        public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.item_entity, parent, false);
            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            DataEntity dataEntity = entities.get(position);
            holder.name.setText(dataEntity.getName());
            holder.id.setText(dataEntity.getUid());
        }

        @Override
        public int getItemCount() {
            // Default is 0 here which will tell our Adapter not to make any Items. This fixes that.
            return entities.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            // Now that we've made our layouts, let's bind them.
            private TextView name;
            private TextView id;
            private ViewGroup container;

            public CustomViewHolder(View itemView) {
                super(itemView);
                this.name = itemView.findViewById(R.id.name);
                this.id = itemView.findViewById(R.id.date);
                this.container = itemView.findViewById(R.id.root_list_item);
                this.container.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //getAdapterPosition() get's an Integer based on which the position of the current
                //ViewHolder (this) in the Adapter. This is how we get the correct Data.
                DataEntity dataEntity = entities.get(
                        this.getAdapterPosition()
                );

                Intent intent = new Intent(getActivity(), GraphActivity.class);
                intent.putExtra("length", dataEntity.getData().length);
                intent.putExtra("data", dataEntity.getData());
                startActivity(intent);

            }
        }

    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            //not used, as the first parameter above is 0
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
//
//            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                dataEntityCollectionViewModel.deleteDataEntity(
                        entities.get(position)
                );

                //ensure View is consistent with underlying data
                entities.remove(position);
                adapter.notifyItemRemoved(position);


            }
        };
        return simpleItemTouchCallback;
    }


}
