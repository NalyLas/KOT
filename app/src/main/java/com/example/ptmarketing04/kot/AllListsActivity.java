package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.ptmarketing04.kot.Adapters.GeneralListAdapter;
import com.example.ptmarketing04.kot.Adapters.GeneralTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AllListsActivity extends Fragment {

    private RecyclerView recViewList;
    private RelativeLayout rlEmpty;

    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private ArrayList<GeneralList> arrayList;
    private int cod;
    GeneralListAdapter adaptador;

    public AllListsActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new Connection();

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recViewList = (RecyclerView) view.findViewById(R.id.recViewList);
        rlEmpty = (RelativeLayout) view.findViewById(R.id.rlEmpty);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        new ListTask().execute();

        return inflater.inflate(R.layout.layout_all_lists, container, false);
    }

    //     Task para cargar las listas del usuario
    class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from LIST where User="+cod);

                jSONArray = conn.sendRequest(Global_params.url_select, parametrosPost);

                if (jSONArray != null) {
                    return jSONArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                arrayList =new ArrayList<GeneralList>();

                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        list = new GeneralList();
                        list.setId(jsonObject.getInt("ID_list"));
                        list.setId_user(jsonObject.getInt("User"));
                        list.setTitle(jsonObject.getString("Title_list"));
                        list.setDate(jsonObject.getString("Date"));
                        arrayList.add(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (arrayList.size()!=0){
                    recViewList.setVisibility(View.VISIBLE);
                    rlEmpty.setVisibility(View.GONE);

                    //Esto no es obligatorio pero si recomendable si siempre va a tener un nº de elementos fijo
                    recViewList.setHasFixedSize(true);

                    final GeneralListAdapter adaptadorl = new GeneralListAdapter(arrayList);
                    adaptadorl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getContext(), MainListActivity.class);
                            i.putExtra("lista",arrayList.get(recViewList.getChildPosition(v)).getId());
                            i.putExtra("title",arrayList.get(recViewList.getChildPosition(v)).getTitle());
                            i.putExtra("user",cod);
                            startActivity(i);
                        }
                    });

                    recViewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recViewList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                    recViewList.setItemAnimator(new DefaultItemAnimator());

                    recViewList.setAdapter(adaptadorl);

                }else{
                    recViewList.setVisibility(View.GONE);
                    rlEmpty.setVisibility(View.VISIBLE);
                }



            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }


}
