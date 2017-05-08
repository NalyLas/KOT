package com.example.ptmarketing04.kot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 05/05/2017.
 */

public class GeneralTaskAdapter extends RecyclerView.Adapter<GeneralTaskAdapter.ListViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<GeneralTask> datos;

    public class ListViewHolder
            extends RecyclerView.ViewHolder {

        private TextView tvTitleTask, tvStartTask, tvEndTask, tvFinished;
        private ImageView ivUrgent;


        public ListViewHolder(final View itemView) {
            super(itemView);

            tvTitleTask = (TextView)itemView.findViewById(R.id.tvTitleTask);
            tvStartTask = (TextView)itemView.findViewById(R.id.tvStartTask);
            tvEndTask = (TextView)itemView.findViewById(R.id.tvEndTask);
            tvFinished = (TextView)itemView.findViewById(R.id.tvFinished);
            ivUrgent = (ImageView)itemView.findViewById(R.id.ivUrgent);
        }

        public void bindList(GeneralTask l) {
            tvTitleTask.setText(l.getTitle());
            tvStartTask.setText(R.string.task_start+" "+l.getStart_date());
            tvEndTask.setText(R.string.task_end+" "+l.getEnd_date());
            if(l.getFinished()==0){
                tvFinished.setText(R.string.no_finish);
            }else{
                tvFinished.setText(R.string.finish);
            }

            if(l.getUrgent()==0){
                ivUrgent.setImageResource(R.mipmap.ic_launcher);
            }else{
                ivUrgent.setImageResource(R.mipmap.ic_launcher_round);
            }

        }
    }

    public GeneralTaskAdapter(ArrayList<GeneralTask> datos) {
        this.datos = datos;
    }

    @Override
    public GeneralTaskAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

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
