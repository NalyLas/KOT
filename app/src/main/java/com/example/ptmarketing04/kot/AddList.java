package com.example.ptmarketing04.kot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AddList extends Fragment {

    protected EditText etName;
  //  protected LinearLayout send_mail;

    public AddList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = (EditText) view.findViewById(R.id.etListName);

      //  send_mail = (LinearLayout)view.findViewById(R.id.btn_send_mail);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_add_list, container, false);
    }
}
