package com.example.ptmarketing04.kot.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Objects.NavItem;
import com.example.ptmarketing04.kot.R;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 11/05/2017.
 */

public class NavAdapter extends BaseAdapter {

    private ArrayList<NavItem> items;
    private Activity activity;


    public NavAdapter(Activity activity, ArrayList<NavItem> items) {
        super();
        this.items = items;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ly = activity.getLayoutInflater();
        View view = ly.inflate(R.layout.item_nav, null, true);

        TextView tvTitulo= (TextView) view.findViewById(R.id.tvNav);
        tvTitulo.setText((String) items.get(position).getItem());

        ImageView im = (ImageView) view.findViewById(R.id.ivNav);
        im.setImageDrawable(items.get(position).getPic());

        return view;
    }
}
