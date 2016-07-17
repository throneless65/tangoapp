package me.abidi.tangoapp;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class CardViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    TextView actionTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        //actionTxtView = (TextView) findViewById(R.id.activateTxtView);
        //Log.d("Text View", actionTxtView.getText().toString());
        /*actionTxtView.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 switch (actionTxtView.getText().toString()){
                                                     case "activate":
                                                         actionTxtView.setText("activated");
                                                         actionTxtView.setTextColor(Color.parseColor("#33cccc"));
                                                     case "activated":
                                                         actionTxtView.setText("activate");
                                                         actionTxtView.setTextColor(Color.parseColor("#ff4081"));
                                                 }
                                             }
                                         });*/
                // Code to Add an item with default animation
                //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

                // Code to remove an item with default animation
                //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
                actionTxtView = (TextView) findViewById(R.id.textView3);
                actionTxtView.setText("activated");
                actionTxtView.setTextColor(Color.parseColor("#33cccc"));
                Snackbar.make(v, "The action is activated.", Snackbar.LENGTH_LONG)
               .setAction("Action", null).show();
            }
        });
    }

    private ArrayList<DataObject> getDataSet() {
        String names[] = {"Turn on the lights when the oven door is open","Notify me when a child is near the open oven","Notify me if I don't take out my ready food"};

        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 3; index++) {
            Bundle extras = getIntent().getExtras();
            //{"device_name": { "action 1": { "title": "", "description": ""}}}
            DataObject obj = new DataObject(extras.getString("DEVICE_NAME") + index,
                    names[index], "activate");
            Log.e("index", String.valueOf(index));
            results.add(index, obj);
        }
        return results;
    }
}
