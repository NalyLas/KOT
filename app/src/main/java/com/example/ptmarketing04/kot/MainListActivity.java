package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainListActivity extends AppCompatActivity {
    private RecyclerView recView;
    private CollapsingToolbarLayout ctlLayout;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private String url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> datos;
    private ArrayList<HashMap<String, String>> allList;
    private int id;

    static public SharedPreferences pref;
    protected String theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("com.example.ptmarketing04.kot_preferences", MODE_PRIVATE);
        theme = pref.getString("theme_pref","OR");
        switch (theme){
            case "OR":
                setTheme(R.style.OrangeTheme);
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                break;
        }

        setContentView(R.layout.activity_main_list);

        //RecyclerView
        recView = (RecyclerView)findViewById(R.id.recView);

        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getInt("lista");
        }

        startTask();

        FloatingActionButton del = (FloatingActionButton) findViewById(R.id.fab);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton edit = (FloatingActionButton) findViewById(R.id.btnFab);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "fsdgtshdjny", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ctlLayout = (CollapsingToolbarLayout)findViewById(R.id.ctlLayout);
        ctlLayout.setTitle("Mi Aplicación");
    }

    private void startTask(){
        recView.removeAllViews();
        new GetTotalTask().execute();

    }


    //     Task para cargar las tareas de la lista actual
    class GetTotalTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from Tareas where Lista = "+id);

                jSONArray = conn.sendRequest(url, parametrosPost);



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
                Log.e("tareas",json.toString());

                datos = new ArrayList<GeneralTask>();

                Log.e("tamaño_----",json.length()+"");
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        task = new GeneralTask();
                        task.setId_task(jsonObject.getInt("ID_tarea"));
                        task.setTitle(jsonObject.getString("Titulo"));
                        task.setStart_date(jsonObject.getString("Fech_inicio"));
                        task.setEnd_date(jsonObject.getString("Fecha_fin"));
                        task.setFinished(jsonObject.getInt("Finalizada"));
                        task.setUrgent(jsonObject.getInt("Urgente"));
                        task.setId_list(jsonObject.getInt("Lista"));

                        Log.e("objeto:",json.getJSONObject(i).getString("Titulo"));

                        datos.add(task);

                        Log.e("tamaño tras:",datos.size()+"");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final GeneralTaskAdapter adaptador = new GeneralTaskAdapter(datos);
                recView.setAdapter(adaptador);

                recView.setLayoutManager(new LinearLayoutManager(MainListActivity.this, LinearLayoutManager.VERTICAL, false));
                recView.addItemDecoration(
                        new DividerItemDecoration(MainListActivity.this, DividerItemDecoration.VERTICAL_LIST));
                recView.setItemAnimator(new DefaultItemAnimator());

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }



    /*
    *    //task para eliminar listas
    class DelListTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ListActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Delete from Listas where ID_lista="+ id +")");

                jsonObject = conn.sendDMLRequest(url_dml, parametrosPost);

                if (jsonObject != null) {
                    return jsonObject;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                try {
                    add = json.getInt("added");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){

                    Snackbar.make(findViewById(android.R.id.content), "eliminado", Snackbar.LENGTH_LONG).show();
                    startTask();

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    * */

}
