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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.ptmarketing04.kot.Adapters.GeneralTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AllTaskActivity extends Fragment {

    private RecyclerView recView;
    private RelativeLayout rlEmpty;
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralTask> datos;
    private ArrayList<GeneralList> allLists;
    private int cod;

    public AllTaskActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new Connection();

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recView = (RecyclerView) view.findViewById(R.id.recView);
        rlEmpty = (RelativeLayout) view.findViewById(R.id.rlEmpty);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        new GetTotalTask().execute();

        return inflater.inflate(R.layout.layout_all_task, container, false);
    }

    //     Task para cargar las tareas de la lista actual
    class GetTotalTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "Select DISTINCT * from TASK, LIST where LIST.User = "+ cod +" and ID_list = List");

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

                datos = new ArrayList<GeneralTask>();

                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        task = new GeneralTask();
                        task.setId_task(jsonObject.getInt("ID_task"));
                        task.setTitle(jsonObject.getString("Title_task"));
                        task.setStart_date(jsonObject.getString("Start_date"));
                        task.setEnd_date(jsonObject.getString("End_date"));
                        task.setFinished(jsonObject.getInt("Finished"));
                        task.setUrgent(jsonObject.getInt("Urgent"));
                        task.setId_list(jsonObject.getInt("List"));

                        datos.add(task);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(datos.size()>0){
                    rlEmpty.setVisibility(View.GONE);
                    recView.setVisibility(View.VISIBLE);
                    final GeneralTaskAdapter adaptador = new GeneralTaskAdapter(datos);

                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getContext(), MainTaskActivity.class);
                            i.putExtra("tarea",datos.get(recView.getChildPosition(v)).getId_task());
                            i.putExtra("titulo",datos.get(recView.getChildPosition(v)).getTitle());
                            i.putExtra("urgente",datos.get(recView.getChildPosition(v)).getUrgent());
                            i.putExtra("acabada",datos.get(recView.getChildPosition(v)).getFinished());
                            i.putExtra("inicio",datos.get(recView.getChildPosition(v)).getStart_date());
                            i.putExtra("fin",datos.get(recView.getChildPosition(v)).getEnd_date());
                            i.putExtra("lista",datos.get(recView.getChildPosition(v)).getId_list());
                            i.putExtra("user",cod);
                            startActivity(i);
                        }
                    });
                    recView.setAdapter(adaptador);

                    recView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                    recView.setItemAnimator(new DefaultItemAnimator());

                }else{
                    rlEmpty.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }



            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }




}
