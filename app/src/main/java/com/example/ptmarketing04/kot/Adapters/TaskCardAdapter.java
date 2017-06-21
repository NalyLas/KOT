package com.example.ptmarketing04.kot.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ptmarketing04.kot.MainTaskActivity;
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
        private LinearLayout llinear;


        public ListViewHolder(final View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            ivUrgent = (ImageView)itemView.findViewById(R.id.ivUrgent);
            llinear = (LinearLayout) itemView.findViewById(R.id.llinear);

        }

        public void bindList(final GeneralTask l) {
            tvTitle.setText(l.getTitle());
            if(l.getUrgent()==0){
                ivUrgent.setImageResource(R.mipmap.ic_launcher);
            }else{
                ivUrgent.setImageResource(R.mipmap.ic_launcher_round);
            }

            llinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(itemView.getContext(), MainTaskActivity.class);
                    i.putExtra("title",l.getTitle());
                    i.putExtra("tarea",l.getId_task());
                    i.putExtra("urgente",l.getUrgent());
                    i.putExtra("acabada",l.getFinished());
                    i.putExtra("inicio",l.getStart_date());
                    i.putExtra("fin",l.getEnd_date());
                    i.putExtra("lista",l.getId_list());
                    itemView.getContext().startActivity(i);

                }
            });

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
