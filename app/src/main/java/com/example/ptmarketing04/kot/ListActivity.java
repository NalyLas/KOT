package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recView;
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
    private int cod,id;

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


        setContentView(R.layout.activity_list);

        recView = (RecyclerView) findViewById(R.id.recView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }

        startTask();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListActivity.this, AddActivity.class);
                i.putExtra("user",cod);
                startActivity(i);
            }
        });


    }

    private void startTask(){
        recView.removeAllViews();
        new ListTask().execute();

    }

    //     Task para cargar las listas del usuario
    class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        private int count=0;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from Listas where user="+cod);

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
                arrayList =new ArrayList<GeneralList>();
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        list = new GeneralList();
                        list.setId(jsonObject.getInt("ID_lista"));
                        list.setId_user(jsonObject.getInt("user"));
                        list.setTitle(jsonObject.getString("Titulo"));
                        list.setDate(jsonObject.getString("Fecha"));
                        arrayList.add(list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



                //Esto no es obligatorio pero si recomendable si siempre va a tener un nº de elementos fijo
                recView.setHasFixedSize(true);

                final GeneralListAdapter adaptador = new GeneralListAdapter(arrayList);


                 //   adaptador.totalTask(datos.size());

                adaptador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ListActivity.this, MainListActivity.class);
                        i.putExtra("lista",arrayList.get(recView.getChildPosition(v)).getId());
                        i.putExtra("title",arrayList.get(recView.getChildPosition(v)).getTitle());
                        i.putExtra("user",cod);
                        startActivity(i);
                    }
                });


                recView.setAdapter(adaptador);

                recView.setLayoutManager(new LinearLayoutManager(ListActivity.this,LinearLayoutManager.VERTICAL,false));
                //recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                // recView.setLayoutManager(new GridLayoutManager(this,3));

                recView.addItemDecoration(
                        new DividerItemDecoration(ListActivity.this,DividerItemDecoration.VERTICAL_LIST));

                //   recView.addItemDecoration(
                //           new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));

                recView.setItemAnimator(new DefaultItemAnimator());

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
    }
}
