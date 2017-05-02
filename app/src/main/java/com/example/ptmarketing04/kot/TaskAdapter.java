package com.example.ptmarketing04.kot;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 02/05/2017.
 */

public class TaskAdapter extends BaseAdapter {
    private ArrayList<GeneralTask> tasks;
    private final Activity activity;


    public TaskAdapter(Activity a, ArrayList<GeneralTask> t){
        super();
        this.tasks = t;
        this.activity = a;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater ly = activity.getLayoutInflater();
        View view = ly.inflate(R.layout.item_task, null, true);

        TextView tv= (TextView) view.findViewById(R.id.tvTitleList);
        tv.setText((CharSequence) tasks.get(position).getTitle());

        TextView tv1= (TextView) view.findViewById(R.id.tvTaskDate);
        tv1.setText(tasks.get(position).getStart_date());

        TextView tv2= (TextView) view.findViewById(R.id.tvTaskFinish);
        tv2.setText(tasks.get(position).getEnd_date());

        TextView tv3= (TextView) view.findViewById(R.id.tvTaskResume);
        tv3.setText(tasks.get(position).getTipe());


        //ImageView im = (ImageView) view.findViewById(R.id.ivItem);


        return view;
    }
}
