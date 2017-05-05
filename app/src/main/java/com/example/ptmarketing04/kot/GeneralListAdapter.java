package com.example.ptmarketing04.kot;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 05/05/2017.
 */

public class GeneralListAdapter extends RecyclerView.Adapter<GeneralListAdapter.ListViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<GeneralList> datos;

    public class ListViewHolder
            extends RecyclerView.ViewHolder {

        private TextView tvTitleList, tvCreated, tvTotalTask;
        private ImageButton btEditList, btDelList;

        public ListViewHolder(final View itemView) {
            super(itemView);

            tvTitleList = (TextView)itemView.findViewById(R.id.tvTitleList);
            tvCreated = (TextView)itemView.findViewById(R.id.tvCreated);
            tvTotalTask = (TextView)itemView.findViewById(R.id.tvTotalTask);
            btEditList = (ImageButton)itemView.findViewById(R.id.btEditList);
            btDelList = (ImageButton)itemView.findViewById(R.id.btDelList);


            btDelList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("datos bton:--->","boton pulsado en "+ getPosition());
                }
            });
        }



        public void bindList(GeneralList l) {
            tvTitleList.setText(l.getTitle());
            tvCreated.setText(l.getDate());
        }
    }

    public GeneralListAdapter(ArrayList<GeneralList> datos) {
        this.datos = datos;
    }


    @Override
    public GeneralListAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        itemView.setOnClickListener(this);
        // android:background="?android:attr/selectableItemBackground"

        ListViewHolder lvh = new ListViewHolder(itemView);

        return lvh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        GeneralList item = datos.get(position);
        holder.bindList(item);

    }

    @Override
    public int getItemCount() {
            return datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

}
