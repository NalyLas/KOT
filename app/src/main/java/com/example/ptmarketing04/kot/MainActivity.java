package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Adapters.ListCardAdapter;
import com.example.ptmarketing04.kot.Adapters.NavAdapter;
import com.example.ptmarketing04.kot.Adapters.UrgentTaskAdapter;
import com.example.ptmarketing04.kot.Objects.ChartTask;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;
import com.example.ptmarketing04.kot.Objects.NavItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private TextView tv,tvUrgent;
    private Toolbar tb;
    private String theme;
    private RecyclerView rvTask,rvList,rvUrgent;
    private BarChart barChart;


    private DrawerLayout dl;
    private ImageView imagec;
    private TextView header_name;
    private LinearLayout llUrgent;
    private ListView lv;
    private ActionBarDrawerToggle action;
    private ArrayList<NavItem> items = new ArrayList<>();
    private NavAdapter nav;

    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ChartTask chartTask,chartTask2;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> datos,arrayTask;
    private ArrayList<ChartTask> chartList,chartList2,chartWeek;
    private int cod,idt,aux;
    private String date, monday, sunday,name;
    private Drawable nav_bckg,urgent_bckg;
    private ArrayList<Integer> colors = new ArrayList<Integer>();

    static public SharedPreferences pref;
    public SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //limpiamos el array de colores
        colors.clear();

        //Modificamos tema en función de las preferencias del usuario
        pref = getSharedPreferences("com.example.ptmarketing04.kot_preferences", MODE_PRIVATE);
        theme = pref.getString("theme_pref","OR");
        switch (theme){
            case "OR":
                setTheme(R.style.OrangeTheme);
                nav_bckg = getDrawable(R.drawable.nav_header_orange);
                urgent_bckg = getDrawable(R.drawable.bg_urgent_orange);
                colors.add(getResources().getColor(R.color.deepOrangePrimary));
                colors.add(getResources().getColor(R.color.deepOrangeAccent));
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                colors.add(getResources().getColor(R.color.blueGrayPrimary));
                colors.add(getResources().getColor(R.color.blueGrayAccent));
                nav_bckg = getDrawable(R.drawable.blue_gray_bck);
                urgent_bckg = getDrawable(R.drawable.bg_urgent_gray);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                colors.add(getResources().getColor(R.color.tealPrimary));
                colors.add(getResources().getColor(R.color.tealAccent));
                nav_bckg = getDrawable(R.drawable.teal_bck);
                urgent_bckg = getDrawable(R.drawable.bg_urgent_teal);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                colors.add(getResources().getColor(R.color.deepPurplePrimary));
                colors.add(getResources().getColor(R.color.deepPurpleAccent));
                nav_bckg = getDrawable(R.drawable.deep_purple_bck);
                urgent_bckg = getDrawable(R.drawable.bg_urgent_purple);
                break;
        }

        setContentView(R.layout.activity_main);

        tb = (Toolbar) findViewById(R.id.toolbar);
        dl = (DrawerLayout) findViewById(R.id.drawer);
        lv = (ListView) findViewById(R.id.lv);

        View header = getLayoutInflater().inflate(R.layout.nav_header, lv, false);
        imagec = (ImageView) header.findViewById(R.id.ivNavHeader);
        header_name = (TextView)header.findViewById(R.id.tvName);

        llUrgent = (LinearLayout)findViewById(R.id.llUrgent);
        llUrgent.setBackground(urgent_bckg);

        rvList = (RecyclerView)findViewById(R.id.rvList);
        rvTask = (RecyclerView)findViewById(R.id.rvTask);
        rvUrgent = (RecyclerView)findViewById(R.id.rvUrgentTask);
        tv = (TextView)findViewById(R.id.tvEmpty);
        tvUrgent = (TextView)findViewById(R.id.tvEmptyUrgent);
        barChart = (BarChart)findViewById(R.id.barChart);

        conn = new Connection();
        createNav();

        nav = new NavAdapter(this,items);
        nav.notifyDataSetChanged();
        lv.addHeaderView(header, null, false);
        lv.setAdapter(nav);

        getNumberTask();

        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        action = new ActionBarDrawerToggle(this, dl, tb, R.string.nav_open, R.string.nav_close);
        dl.addDrawerListener(action);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
            name = extras.getString("name");
        }

        header.setBackgroundColor(colors.get(0));
        header_name.setText(name);
    }

    public void createNav(){
        items.clear();

        //Creamos los elementos del menu
        items.add(new NavItem(0,getResources().getDrawable(R.mipmap.nav_all_list),getResources().getString(R.string.nav_all_list)));
        items.add(new NavItem(1,getResources().getDrawable(R.mipmap.nv_all_task),getResources().getString(R.string.nav_all_task)));
        items.add(new NavItem(2,getResources().getDrawable(R.mipmap.nav_add_list),getResources().getString(R.string.nav_add_list)));
        items.add(new NavItem(3,getResources().getDrawable(R.mipmap.nav_add_task),getResources().getString(R.string.nav_add_task)));
        items.add(new NavItem(4,getResources().getDrawable(R.mipmap.nav_urgent),getResources().getString(R.string.nav_urgent)));
        items.add(new NavItem(5,getResources().getDrawable(R.mipmap.nav_chart),getResources().getString(R.string.nav_chart)));
        items.add(new NavItem(6,getResources().getDrawable(R.mipmap.nav_logout),getResources().getString(R.string.nav_logout)));


        //Creamos los eventos para ir a las distintas actividades
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (items.get(position-1).getId()) {
                    case 0:
                        intent = new Intent(MainActivity.this,ListActivity.class);
                        intent.putExtra("user",cod);
                        intent.putExtra("tab_activa",0);
                        startActivity(intent);
                        dl.closeDrawer(Gravity.LEFT);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this,ListActivity.class);
                        intent.putExtra("user",cod);
                        intent.putExtra("tab_activa",1);
                        startActivity(intent);
                        dl.closeDrawer(Gravity.LEFT);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this,AddActivity.class);
                        intent.putExtra("user",cod);
                        intent.putExtra("tab_activa",0);
                        startActivity(intent);
                        dl.closeDrawer(Gravity.LEFT);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this,AddActivity.class);
                        intent.putExtra("user",cod);
                        intent.putExtra("tab_activa",1);
                        startActivity(intent);
                        dl.closeDrawer(Gravity.LEFT);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this,UrgentListActivity.class);
                        intent.putExtra("user",cod);
                        startActivity(intent);
                        break;
                    case 5:
                       /* intent = new Intent(MainActivity.this,ListActivity.class);
                        intent.putExtra("user",cod);
                        intent.putExtra("tab_activa",0);
                        startActivity(intent);*/
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this,LoginActivity.class);
                        intent.putExtra("email",pref.getString("email",""));
                        editor = pref.edit();
                        editor.putBoolean("pre_login",false);
                        editor.putString("user","");
                        editor.putString("pass","");
                        editor.putInt("cod",0);
                        editor.commit();
                        startActivity(intent);
                        break;

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.list).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.important).setVisible(false);
        menu.findItem(R.id.chart).setVisible(false);
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
            case R.id.list:
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


   //Tendras que averiguar una forma más optima de realizar este proceso
    //De esta forma por ahora es la unica que funciona
    //Pero carga todas las tareas de la tabla y luego comprueba una a una
    // Cuano existan muchas tareas irá muy lento
    //Intenta crear un nuevo array cada vez que lances la task y averigua como pasarle todos los ids
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

   public void createChart(){

       BarData data = new BarData(getDaysValues(), getDataSet());
       barChart.setData(data);
       barChart.setDescription("");
       barChart.animateXY(3000, 3000);
       barChart.invalidate();

   }

    public void fillArray(ArrayList<ChartTask> array){
        for(int i=0;i<array.size();i++){
            for(int j=0;j<chartWeek.size();j++){
                if(array.get(i).getEndDate().equals(chartWeek.get(j).getEndDate())){
                    chartWeek.get(j).setNumber(array.get(i).getNumber());
                }
            }
        }
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        //Tareas NO urgentes
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        //Rellenamos los datos
        for(int i=0;i<7;i++){
            valueSet1.add(new BarEntry(chartList2.get(i).getNumber(),i));
        }


        //Tareas URGENTES
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        //Rellenamos los datos
        for(int i=0;i<7;i++){
            valueSet2.add(new BarEntry(chartList.get(i).getNumber(),i));
        }


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, getResources().getString(R.string.val_no_urgent));
        barDataSet1.setColor(colors.get(0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, getResources().getString(R.string.val_urgent));
        barDataSet2.setColor(colors.get(1));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getDaysValues() {
        ArrayList<String> days = new ArrayList<>();
        days.add(getResources().getString(R.string.mon));
        days.add(getResources().getString(R.string.tue));
        days.add(getResources().getString(R.string.wed));
        days.add(getResources().getString(R.string.thur));
        days.add(getResources().getString(R.string.fri));
        days.add(getResources().getString(R.string.sat));
        days.add(getResources().getString(R.string.sun));
        return days;
    }

    public Date getWeek(Date fecha, int dias){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0

        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    private void getNumberTask(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date = df.format(c.getTime());
        String p = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        int m = 0;
        int s = 0;
        switch (Integer.parseInt(p)){
            case 1:
                m = -6;
                s = 0;
                break;
            case 2:
                m = 0;
                s = 6;
                break;
            case 3:
                m = -1;
                s = 5;
                break;
            case 4:
                m = -2;
                s = 4;
                break;
            case 5:
                m = -3;
                s = 3;
                break;
            case 6:
                m = -4;
                s = 2;
                break;
            case 7:
                m = -5;
                s = 1;
                break;
        }

        monday = df.format(getWeek(c.getTime(),m));
        sunday = df.format(getWeek(c.getTime(),s));
        chartWeek = new ArrayList<ChartTask>();
        chartWeek.add(new ChartTask(0,monday));
        try {
            chartWeek.add(new ChartTask(0,df.format(getWeek(df.parse(monday),1))));
            chartWeek.add(new ChartTask(0,df.format(getWeek(df.parse(monday),2))));
            chartWeek.add(new ChartTask(0,df.format(getWeek(df.parse(monday),3))));
            chartWeek.add(new ChartTask(0,df.format(getWeek(df.parse(monday),4))));
            chartWeek.add(new ChartTask(0,df.format(getWeek(df.parse(monday),5))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        chartWeek.add(new ChartTask(0,sunday));

        new GetChartUrgentTask().execute();
        new GetChartNormalTask().execute();
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


                        arrayList.add(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(arrayList.size()>0){
                    fillTask();

                    for(int i=0;i<arrayList.size();i++){
                        if(arrayList.get(i).getTasks().size()==0){
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
                    createChart();
                    tvUrgent.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);

                    urgentTask();


                }else{
                    rvList.setVisibility(View.GONE);
                    tvUrgent.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
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
                parametrosPost.put("ins_sql", "Select * from TASK");

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

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //     Task para cargar el grafico de tareas de la semana del usuario (URGENTES)
    class GetChartUrgentTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "SELECT COUNT(*) AS total_number, `End_date` AS fecha FROM `TASK` WHERE `User`= "+pref.getInt("cod",0)+" AND  `Urgent`= 1 AND `End_date` BETWEEN '"+ monday +"' AND '"+ sunday +"' GROUP BY `End_date`");

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

                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        chartTask = new ChartTask();
                        chartTask.setNumber(jsonObject.getInt("total_number"));
                        chartTask.setEndDate(jsonObject.getString("fecha"));

                        chartList.add(chartTask);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                fillArray(chartList);
                chartList = chartWeek;

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //     Task para cargar el grafico de tareas de la semana del usuario (NO URGENTES)
    class GetChartNormalTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "SELECT COUNT(*) AS total_number, `End_date` AS fecha FROM `TASK` WHERE `User`= "+pref.getInt("cod",0)+" AND  `Urgent`= 0 AND `End_date` BETWEEN '"+ monday +"' AND '"+ sunday +"' GROUP BY `End_date`");

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

                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        chartTask2 = new ChartTask();
                        chartTask2.setNumber(jsonObject.getInt("total_number"));
                        chartTask2.setEndDate(jsonObject.getString("fecha"));

                        chartList2.add(chartTask2);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                fillArray(chartList2);
                chartList2 = chartWeek;

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        action.syncState();
        aux = 0;
        datos = new ArrayList<GeneralTask>();
        arrayTask = new ArrayList<GeneralTask>();
        chartList = new ArrayList<ChartTask>();
        chartList2 = new ArrayList<ChartTask>();
        new GetTotalTask().execute();
        new ListTask().execute();
    }

}