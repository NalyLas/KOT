package com.example.ptmarketing04.kot.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Objects.GeneralTask;
import com.example.ptmarketing04.kot.R;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 05/05/2017.
 */

public class TaskCardAdapter extends RecyclerView.Adapter<TaskCardAdapter.ListViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<GeneralTask> datos;

    public class ListViewHolder
            extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivUrgent;


        public ListViewHolder(final View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            ivUrgent = (ImageView)itemView.findViewById(R.id.ivUrgent);
        }

        public void bindList(GeneralTask l) {
            tvTitle.setText(l.getTitle());
            if(l.getUrgent()==0){
                ivUrgent.setImageResource(R.mipmap.ic_launcher);
            }else{
                ivUrgent.setImageResource(R.mipmap.ic_launcher_round);
            }

        }
    }

    public TaskCardAdapter(ArrayList<GeneralTask> datos) {
        this.datos = datos;
    }

    @Override
    public TaskCardAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_task, parent, false);

        itemView.setOnClickListener(this);
        // android:background="?android:attr/selectableItemBackground"

        ListViewHolder lvh = new ListViewHolder(itemView);

        return lvh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        GeneralTask item = datos.get(position);
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
