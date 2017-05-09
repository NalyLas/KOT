package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Adapters.GeneralTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    protected LinearLayout llist;
    protected TextView tv;
    protected Toolbar tb;
    protected String theme;
    protected RecyclerView rvTask;
   // protected ListView lvTask;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> datos,arrayTask;
    private ArrayList<HashMap<String, String>> allList;
    private int cod,idt,aux;

    static public SharedPreferences pref;
    Color color;


    @RequiresApi(api = Build.VERSION_CODES.M)
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


        setContentView(R.layout.activity_main);
        tb = (Toolbar) findViewById(R.id.toolbar);
        llist = (LinearLayout)findViewById(R.id.linerat_list);

        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();

        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }

        aux = 0;

        llist.removeAllViews();
        new GetTotalTask().execute();
        new ListTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.preferencias:
                i = new Intent(this,Preferences.class);
                startActivity(i);
                return true;
            case R.id.task:
                i = new Intent(this,ListActivity.class);
                i.putExtra("user",cod);
                startActivity(i);
                return true;
            case R.id.add:
                i = new Intent(this,AddActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                return true;

            case R.id.important:
            //    i = new Intent(this,ListActivity.class);
            //    i.putExtra("user",cod);
            //    startActivity(i);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    private void addChild() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.list_card;

        CardView cardView = (CardView)inflater.inflate(id,null,false);
        tv = (TextView)cardView.findViewById(R.id.list_title);
        rvTask = (RecyclerView) cardView.findViewById(R.id.rvTask);


       // TextView textView = (TextView) relativeLayout.findViewById(R.id.textViewDate);
       // textView.setText(String.valueOf(System.currentTimeMillis()));

        llist.addView(cardView);
    }


    //     Task para cargar las listas del usuario
    class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
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
                        arrayList.add(list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(int l=0; l<arrayList.size(); l++){

                    //Esto no funciona bien!!!!!!
                    //Prueba  hacerte un recyclerview horizontal con su adaptador
                    //que contenga las cardviews y pasarle todos los datos que necesitas
                    //de la form que esta hecho se repiten las tareas
                    arrayTask.clear();

                    addChild();
                    tv.setText(arrayList.get(l).getTitle());
                    idt = arrayList.get(l).getId();

                    for(int k=0; k<datos.size();k++){
                        if(idt == datos.get(k).getId_list()){
                            arrayTask.add(datos.get(k));
                        }
                    }

                    //   emptyList.setVisibility(View.GONE);
                    rvTask.setVisibility(View.VISIBLE);
                    final GeneralTaskAdapter adaptador = new GeneralTaskAdapter(arrayTask);
                    rvTask.setAdapter(adaptador);


                    rvTask.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                    rvTask.addItemDecoration(
                            new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL_LIST));
                    rvTask.setItemAnimator(new DefaultItemAnimator());

                }


            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //     Task para cargar las tareas de la lista actual
    class GetTotalTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from Tareas");

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

                datos = new ArrayList<GeneralTask>();
                arrayTask = new ArrayList<GeneralTask>();

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

                        datos.add(task);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }
}
