package com.example.ptmarketing04.kot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AddList extends Fragment {

    protected EditText etName;
    protected Button addlist;

    public AddList() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = (EditText) view.findViewById(R.id.etListName);
        addlist = (Button) view.findViewById(R.id.btAddList);

        addlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_add_list, container, false);
    }
}
