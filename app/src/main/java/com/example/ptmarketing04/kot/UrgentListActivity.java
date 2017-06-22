package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.ptmarketing04.kot.Adapters.GeneralTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UrgentListActivity extends AppCompatActivity {

    private Toolbar tb;
    private RelativeLayout rlEmpty;
    private RecyclerView recView;
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralTask> datos;
    private ArrayList<GeneralList> arrayList;
    private int cod;

    static public SharedPreferences pref;
    private String theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Modificamos tema en función de las preferencias del usuario
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

        setContentView(R.layout.activity_urgent_list);

        //añadimos toolbar
        tb = (Toolbar) findViewById(R.id.toolbar);
        recView = (RecyclerView)findViewById(R.id.recView);
        rlEmpty = (RelativeLayout) findViewById(R.id.rlEmpty);


        conn = new Connection();

        if(tb != null){
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }

        new GetUrgentTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.preferencias).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.add:
                i = new Intent(this,AddActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;

            case R.id.list:
                i = new Intent(this,ListActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;

            case R.id.chart:
                i = new Intent(this,SummaryActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //     Task para cargar las tareas urgentes del usuario
    class GetUrgentTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(UrgentListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from TASK where `Urgent`=1 AND `User` ="+cod);

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
                            Intent i = new Intent(UrgentListActivity.this, MainTaskActivity.class);
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

                    recView.setLayoutManager(new LinearLayoutManager(UrgentListActivity.this, LinearLayoutManager.VERTICAL, false));
                    recView.addItemDecoration(new DividerItemDecoration(UrgentListActivity.this, DividerItemDecoration.VERTICAL_LIST));
                    recView.setItemAnimator(new DefaultItemAnimator());

                }else{
                    rlEmpty.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }



            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

}
