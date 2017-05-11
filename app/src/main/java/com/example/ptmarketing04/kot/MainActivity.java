package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Adapters.ListCardAdapter;
import com.example.ptmarketing04.kot.Adapters.UrgentTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    protected TextView tv,tvUrgent;
    protected Toolbar tb;
    protected String theme;
    protected RecyclerView rvTask,rvList,rvUrgent;
   // protected ListView lvTask;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> datos,arrayTask;
    private int cod,idt,aux;

    static public SharedPreferences pref;

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        setContentView(R.layout.activity_main);

        tb = (Toolbar) findViewById(R.id.toolbar);
        rvList = (RecyclerView)findViewById(R.id.rvList);
        rvTask = (RecyclerView)findViewById(R.id.rvTask);
        rvUrgent = (RecyclerView)findViewById(R.id.rvUrgentTask);
        tv = (TextView)findViewById(R.id.tvEmpty);
        tvUrgent = (TextView)findViewById(R.id.tvEmptyUrgent);


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

      /*  aux = 0;
        datos = new ArrayList<GeneralTask>();
        arrayTask = new ArrayList<GeneralTask>();
        new GetTotalTask().execute();
        new ListTask().execute();*/

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
                i.putExtra("user",cod);
                startActivity(i);
                finish();
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


   /* private void addChild() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.list_card;

        CardView cardView = (CardView)inflater.inflate(id,null,false);
        tv = (TextView)cardView.findViewById(R.id.list_title);
        rvTask = (RecyclerView) cardView.findViewById(R.id.rvTask);


       // TextView textView = (TextView) relativeLayout.findViewById(R.id.textViewDate);
       // textView.setText(String.valueOf(System.currentTimeMillis()));

        llist.addView(cardView);
    }*/

   //Tendras que averif¡guar una forma más optima de realizar este proceso
    //De esta forma por ahora es la unica que funciona
    //Pero carga todas las tareas de la tabla y luego comprueba una a una
    // Cuano existan muchas tareas irá muy lento
    //Intenta crear un nuevo array cada vez que lances la task y averigua como pararle todos los ids
    //Sienpre se saltaba el primero

    //Obtenemos las tareas de cada lista
   public void fillTask(){
       aux = 0;
       while(aux<arrayList.size()){
           idt = arrayList.get(aux).getId();
           ArrayList<GeneralTask> array = new ArrayList<>();
           for(int i=0;i<datos.size();i++){
               if(idt == datos.get(i).getId_list()){
                  array.add(datos.get(i));
               }
           }

           arrayList.get(aux).setTasks(array);
           aux++;
       }

   }

   //Planteate hacer esto filtrando por tareas urgentes
    // select * from task where urgent = 1 and finished = 0 order by fecha_final ASC;
    // es decir obtener solo aquellas tareas que sean urgentes y no esten finalizadas

    //Obtenemos las tareas urgentes
   public void urgentTask(){
       aux = 0;
       while(aux<arrayList.size()){
           arrayTask = new ArrayList<>();
           for(int i=0;i<datos.size();i++){
               if(datos.get(i).getFinished()==0 && datos.get(i).getUrgent()==1){
                   arrayTask.add(datos.get(i));
               }
           }
           aux++;
       }

       if (arrayTask.size()>0){

           final UrgentTaskAdapter adaptadoru = new UrgentTaskAdapter(arrayTask);

           adaptadoru.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(MainActivity.this, MainTaskActivity.class);
                   i.putExtra("tarea",arrayTask.get(rvUrgent.getChildPosition(v)).getId_task());
                   i.putExtra("title",arrayTask.get(rvUrgent.getChildPosition(v)).getTitle());
                   i.putExtra("user",cod);
                   startActivity(i);
               }
           });


           rvUrgent.setAdapter(adaptadoru);
           rvUrgent.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

           tvUrgent.setVisibility(View.GONE);
           rvUrgent.setVisibility(View.VISIBLE);

       }else{
           rvUrgent.setVisibility(View.GONE);
           tvUrgent.setVisibility(View.VISIBLE);
       }
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
                        list.setTitle(jsonObject.getString("Titulo_lista"));


                        arrayList.add(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(arrayList.size()>0){
                    fillTask();

                    for(int i=0;i<arrayList.size();i++){
                        if(arrayList.get(i).getTasks().size()<1){
                            arrayList.remove(i);
                        }
                    }



                    //Creamos el adapatador
                    final ListCardAdapter adaptador = new ListCardAdapter(arrayList);

                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MainActivity.this, MainListActivity.class);
                            i.putExtra("lista",arrayList.get(rvList.getChildPosition(v)).getId());
                            i.putExtra("title",arrayList.get(rvList.getChildPosition(v)).getTitle());
                            i.putExtra("user",cod);
                            startActivity(i);
                        }
                    });

                    rvList.setAdapter(adaptador);
                    rvList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

                    tv.setVisibility(View.GONE);
                    rvList.setVisibility(View.VISIBLE);

                    urgentTask();


                }else{
                    rvList.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                }


            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    //     Task para cargar las tareas del usuario
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

    @Override
    protected void onResume() {
        super.onResume();
        aux = 0;
        datos = new ArrayList<GeneralTask>();
        arrayTask = new ArrayList<GeneralTask>();
        new GetTotalTask().execute();
        new ListTask().execute();
    }
}
