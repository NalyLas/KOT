package com.example.ptmarketing04.kot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ptmarketing04.kot.Objects.GeneralContent;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTaskActivity extends AppCompatActivity {

    private TextView tvDate,tvUrgent,tvInfo,tvList,tvFinish,tvCreated;
    private CollapsingToolbarLayout ctlLayout;
    private ImageView imgToolbar;

    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private GeneralContent content;
    private ArrayList<HashMap<String, String>> allList;
    private int id,cod,urgent,finish,cod_list;
    private String title,start,end,info, title_list;
    private Drawable nav_bckg;

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
                nav_bckg = getResources().getDrawable(R.drawable.deep_orange_bck);
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                nav_bckg = getResources().getDrawable(R.drawable.blue_gray_bck);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                nav_bckg = getResources().getDrawable(R.drawable.teal_bck);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                nav_bckg = getResources().getDrawable(R.drawable.deep_purple_bck);
                break;
        }

        setContentView(R.layout.activity_main_task);

        imgToolbar = (ImageView) findViewById(R.id.imgToolbar);
        tvUrgent = (TextView)findViewById(R.id.tvUrgent);
        tvCreated = (TextView)findViewById(R.id.tvCreated);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvInfo = (TextView)findViewById(R.id.tvInfo);
        tvList = (TextView)findViewById(R.id.tvList);
        tvFinish = (TextView)findViewById(R.id.tvFinish);
        ctlLayout = (CollapsingToolbarLayout)findViewById(R.id.ctlLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        conn = new Connection();

        imgToolbar.setImageDrawable(nav_bckg);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getInt("tarea");
            title = extras.getString("title");
            start = extras.getString("inicio");
            end = extras.getString("fin");
            finish = extras.getInt("acabada");
            urgent = extras.getInt("urgente");
            cod_list = extras.getInt("lista");
            cod = extras.getInt("user");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton del = (FloatingActionButton) findViewById(R.id.fab);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alert
                AlertDialog.Builder builder = new AlertDialog.Builder(MainTaskActivity.this);
                builder.setTitle(getResources().getString(R.string.del_task));
                builder.setMessage(getResources().getString(R.string.del_task_text));
                builder.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Comprobamos si la lista tiene tareas asociadas
                                   new DeleteContentTask().execute();
                            }
                        });

                builder.setNegativeButton(getResources().getString(R.string.cancel),null);
                builder.create();
                builder.show();


            }
        });


        new GetListTask().execute();

    }

    public void fillInfo(){
        ctlLayout.setTitle(title);
        tvCreated.setText(getResources().getString(R.string.created)+" "+start);
        if(urgent ==1){
            tvUrgent.setText(getResources().getString(R.string.task_urgent)+".");
        }else{
            tvUrgent.setText(getResources().getString(R.string.task_no_urgent)+".");
        }

        if(finish ==1){
            tvFinish.setText(getResources().getString(R.string.finish));
        }else{
            tvFinish.setText(getResources().getString(R.string.no_finish));
        }

        tvDate.setText(getResources().getString(R.string.task_date)+" "+end);
        tvList.setText(title_list);
        tvInfo.setText(info);
    }

    //     Task para cargar los detalles de la tarea
    class GetContentTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainTaskActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from CONTENT where `Task`="+id);

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
                        content = new GeneralContent();
                        content.setId(jsonObject.getInt("ID_content"));
                        content.setDetail(jsonObject.getString("Info"));
                        content.setTipe(jsonObject.getInt("Type"));
                        content.setTask(jsonObject.getInt("Task"));

                        info = content.getDetail();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                fillInfo();

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //     Task para cargar la lista a la que pertenece la tarea
    class GetListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainTaskActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from LIST where `ID_list` ="+cod_list);

                Log.e("consulta","----> "+parametrosPost);

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
                        list = new GeneralList();
                        list.setId(jsonObject.getInt("ID_list"));
                        list.setId_user(jsonObject.getInt("User"));
                        list.setTitle(jsonObject.getString("Title_list"));

                        title_list = list.getTitle();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                new GetContentTask().execute();

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para eliminar listas
    class DeleteTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainTaskActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Delete from TASK where ID_task="+ id);

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

                    Snackbar.make(findViewById(android.R.id.content), "eliminado", Snackbar.LENGTH_LONG).show();
                    Intent i = new Intent(MainTaskActivity.this, ListActivity.class);
                    i.putExtra("user",cod);
                    startActivity(i);

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para eliminar listas
    class DeleteContentTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainTaskActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Delete from CONTENT where Task="+ id);

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
                    Snackbar.make(findViewById(android.R.id.content), "eliminado", Snackbar.LENGTH_LONG).show();
                    new DeleteTask().execute();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }


}
