package com.example.ptmarketing04.kot.Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;
import com.example.ptmarketing04.kot.R;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 05/05/2017.
 */

public class ListCardAdapter extends RecyclerView.Adapter<ListCardAdapter.ListViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<GeneralList> listas;
    private ArrayList<GeneralTask> tasks;
    private TextView list_title;
    private RecyclerView rcv;

    public class ListViewHolder
            extends RecyclerView.ViewHolder {


        public ListViewHolder(final View itemView) {
            super(itemView);

            list_title = (TextView)itemView.findViewById(R.id.list_title);
            rcv = (RecyclerView) itemView.findViewById(R.id.rvTask);
        }

        public void bindList(GeneralList l) {
            list_title.setText(l.getTitle());

            rcv.removeAllViews();

            final TaskCardAdapter adaptador2 = new TaskCardAdapter(l.getTasks());
            rcv.setAdapter(adaptador2);
            rcv.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
        }
    }

    public ListCardAdapter(ArrayList<GeneralList> datos) {
        this.listas = datos;
    }

    @Override
    public ListCardAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card, parent, false);

        itemView.setOnClickListener(this);

        ListViewHolder lvh = new ListViewHolder(itemView);

        return lvh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        GeneralList item = listas.get(position);
        holder.bindList(item);

    }

    @Override
    public int getItemCount() {
            return listas.size();
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
