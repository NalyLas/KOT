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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ptmarketing04.kot.Adapters.GeneralTaskAdapter;
import com.example.ptmarketing04.kot.Objects.GeneralList;
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainListActivity extends AppCompatActivity {

    private RecyclerView recView;
    private CollapsingToolbarLayout ctlLayout;
    private Dialog dialog;
    private EditText etNewTitle;
    private RelativeLayout emptyList;
    private ImageView imgToolbar;

    private JSONArray jSONArray;
    private JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private ArrayList<GeneralTask> datos;
    private int id,cod;
    private String title,new_title;
    private Drawable nav_bckg;

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

        setContentView(R.layout.activity_main_list);
        new_title = null;

        //RecyclerView
        recView = (RecyclerView)findViewById(R.id.recView);
        emptyList = (RelativeLayout) findViewById(R.id.emptyList);
        imgToolbar = (ImageView) findViewById(R.id.imgToolbar);
        //Dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setTitle(getResources().getString(R.string.update_title));

        etNewTitle = (EditText)dialog.findViewById(R.id.etNewTitle);

        imgToolbar.setImageDrawable(nav_bckg);


        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.btAcept).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new_title = etNewTitle.getText().toString();
                if(new_title != null && !new_title.equals(" ") && !new_title.equals("")){
                    new UpdateListTask().execute();
                }else{
                    Snackbar.make(view, getResources().getString(R.string.empty_text), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        //Accion de boton cancelar
        dialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        conn = new Connection();


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getInt("lista");
            title = extras.getString("title");
            cod = extras.getInt("user");
        }

        startTask();

        FloatingActionButton del = (FloatingActionButton) findViewById(R.id.fab);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alert
                AlertDialog.Builder builder = new AlertDialog.Builder(MainListActivity.this);
                builder.setTitle(getResources().getString(R.string.del_list));
                builder.setMessage(getResources().getString(R.string.del_list_text));
                builder.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Comprobamos si la lista tiene tareas asociadas
                              if(datos.size()>0){
                                  new DeleteTotalListTask().execute();
                              }else{
                                  new DelListTask().execute();
                              }
                            }
                        });

                builder.setNegativeButton(getResources().getString(R.string.cancel),null);
                builder.create();
                builder.show();


            }
        });

        FloatingActionButton edit = (FloatingActionButton) findViewById(R.id.btnFab);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

            }
        });

        ctlLayout = (CollapsingToolbarLayout)findViewById(R.id.ctlLayout);
        ctlLayout.setTitle(title);
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
                parametrosPost.put("ins_sql", "Select * from TASK where List = "+id);

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
                    emptyList.setVisibility(View.GONE);
                    recView.setVisibility(View.VISIBLE);
                    final GeneralTaskAdapter adaptador = new GeneralTaskAdapter(datos);

                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MainListActivity.this, MainTaskActivity.class);
                            i.putExtra("tarea",datos.get(recView.getChildPosition(v)).getId_task());
                            i.putExtra("title",datos.get(recView.getChildPosition(v)).getTitle());
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

                    recView.setLayoutManager(new LinearLayoutManager(MainListActivity.this, LinearLayoutManager.VERTICAL, false));
                    recView.addItemDecoration(
                            new DividerItemDecoration(MainListActivity.this, DividerItemDecoration.VERTICAL_LIST));
                    recView.setItemAnimator(new DefaultItemAnimator());

                }else{
                    emptyList.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }


    //     Task para modificar el titulo de la lista
    class UpdateListTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainListActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Update LIST SET `Title_list` = '"+ new_title +"' where `ID_list`="+ id);

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

                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.content_update), Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    ctlLayout.setTitle(new_title);

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para eliminar las tareas asociadas a la lista
    class DeleteTotalListTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainListActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Delete from TASK where List="+ id);

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
                    new DelListTask().execute();

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para eliminar listas
    class DelListTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainListActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Delete from LIST where ID_list="+ id);

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
                    Intent i = new Intent(MainListActivity.this, ListActivity.class);
                    i.putExtra("user",cod);
                    i.putExtra("tab_activa",0);
                    startActivity(i);

                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

}
