package com.example.ptmarketing04.kot;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.ptmarketing04.kot.Objects.GeneralContent;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditTaskActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText etTitle, etDetail, etDate;
    private Button addTask;
    private ImageButton addDate;
    private CheckBox cbUrgent;
    private Toolbar tb;

    private JSONArray jSONArray;
    private JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private GeneralContent content;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> arrayTask;
    private ArrayList<GeneralContent> arrayContent;
    private int cod,urgent,idt,idl,idc,finish;
    private String date,title,dateEnd, details;
    private boolean empty;

    static public SharedPreferences pref;
    protected String theme;


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

        setContentView(R.layout.activity_edit_task);


        etTitle = (EditText) findViewById(R.id.etTitleTask);
        etDetail = (EditText) findViewById(R.id.etDetailTask);
        etDate = (EditText) findViewById(R.id.etDate);
        spinner = (Spinner) findViewById(R.id.spinner_list);
        cbUrgent = (CheckBox) findViewById(R.id.cbUrgent);

        addTask = (Button) findViewById(R.id.btAddTask);
        addDate = (ImageButton) findViewById(R.id.btDate);

        conn = new Connection();

        //añadimos toolbar
        tb = (Toolbar) findViewById(R.id.toolbar);

        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            idt = extras.getInt("tarea");
            title = extras.getString("title");
            dateEnd = extras.getString("fin");
            finish = extras.getInt("acabada");
            urgent = extras.getInt("urgente");
            idl = extras.getInt("lista");
            details = extras.getString("detail");
            cod = extras.getInt("user");

            etTitle.setText(title);
            etDetail.setText(details);
            etDate.setText(dateEnd);

            if(details.equals(null) || details.equals("")){
                empty = true;
            }else{
                empty = false;
                etDetail.setText(details);
            }

            if(urgent == 1){
                cbUrgent.setChecked(true);
            }else{
                cbUrgent.setChecked(false);
            }
        }

        addDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //alert
                final Calendar c = Calendar.getInstance();
                final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(EditTaskActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date =dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        try {
                            Date d = df.parse(date);
                            etDate.setText(df.format(d));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                dpd.show();

            }
        });

        addTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              /*  getParams();

                new AddTask.AddNewTask().execute();
                //Recargamos tareas
                new AddTask.GetTotalTask().execute();
                new AddTask.GetContentTask().execute();*/

            }
        });
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
            case R.id.list:
                i = new Intent(this,ListActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;
            case R.id.important:
                i = new Intent(this,UrgentListActivity.class);
                i.putExtra("user",cod);
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

    public void getParams(){

        //Comprobamos la fecha limite de la tarea
        if(etDate.getText().toString().equals("") || etDate.getText() == null){
            Snackbar.make(findViewById(android.R.id.content), "Elija una fecha de finalización para la tarea", Snackbar.LENGTH_LONG).show();
        }else{
            dateEnd = etDate.getText().toString();
        }

        //Comprobamos el titulo de la tarea
        if(!etTitle.getText().toString().equals(" ") && !etTitle.getText().equals(null)){
            title = etTitle.getText().toString();
        }else{
            Snackbar.make(findViewById(android.R.id.content), "Debe introducir un titulo para la tarea", Snackbar.LENGTH_LONG).show();
        }

        //obtenemos el id de la lista a la que pertenece
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getTitle().equals(spinner.getSelectedItem())){
                idl = arrayList.get(i).getId();
                break;
            }
        }

        //calculamos el id del contenido
        if(arrayContent.size()>0){
            idc = arrayContent.get(arrayContent.size()-1).getId()+1;
        }else{
            idc=0;
        }

        //comprobamos si es una tarea urgente
        if(cbUrgent.isChecked()) {
            urgent = 1;
        }else{
            urgent = 0;
        }

        //Comprobamos los detalles
        details = etDetail.getText().toString();

    }

    //task para modificar tareas
    class UpdateTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditTaskActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE `TASK` SET `Title_task`='"+ title +"',`End_date`='"+ dateEnd +"',`Urgent`="+ urgent +",`List`="+idl+",`User`="+ cod +" WHERE `ID_task`="+idt);

                jsonObject = conn.sendDMLRequest(Global_params.url_dml, parametrosPost);

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

                    Snackbar.make(findViewById(android.R.id.content), "añadido", Snackbar.LENGTH_LONG).show();
                    new UpdateContentTask().execute();

                }else{
                    Snackbar.make(findViewById(android.R.id.content), "un carajo", Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para agregar contenido a las tareas
    class UpdateContentTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditTaskActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE `CONTENT` SET `Info`='"+ details +"',`Task`="+ idt +" WHERE `ID_content`="+idc);

                jsonObject = conn.sendDMLRequest(Global_params.url_dml, parametrosPost);

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

                    Log.e("añadido detaaalle??", add+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){

                    Snackbar.make(findViewById(android.R.id.content), "añadido", Snackbar.LENGTH_LONG).show();

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para agregar contenido a las tareas
    class AddContentTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditTaskActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "INSERT INTO `CONTENT`(`ID_content`, `Type`, `Info`, `Task`) VALUES ("+ idc +",1,"+ details +",'"+ idt +"')");

                jsonObject = conn.sendDMLRequest(Global_params.url_dml, parametrosPost);

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

                    Log.e("añadido detaaalle??", add+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){

                    Snackbar.make(findViewById(android.R.id.content), "añadido", Snackbar.LENGTH_LONG).show();

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }
}
