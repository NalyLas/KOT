package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    protected TextView tv,tv2,tv3;
    protected Toolbar tb;
    protected LinearLayout llg;
    protected String theme;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<HashMap<String, String>> allList;
    private int cod;

    static public SharedPreferences pref;
    Color color;

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

        llg = (LinearLayout) findViewById(R.id.llGeneral);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
            Log.e("codigo",cod+"");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListActivity.this, AddActivity.class);
                i.putExtra("user",cod);
                startActivity(i);
            }
        });


        startTask();
    }



    private void startTask(){
        llg.removeAllViews();
        new ListTask().execute();
    }

    private void addCard() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.add_list_card;

        CardView cardView = (CardView)inflater.inflate(id,null,false);
        tv = (TextView)cardView.findViewById(R.id.tvTitleList);
        //Añadir campo de fehca de creación para añadirlo a este textview
        tv2 = (TextView)cardView.findViewById(R.id.tvCreated);
        tv3 = (TextView)cardView.findViewById(R.id.tvTotalTask);

        llg.addView(cardView);

    }



    //     Task para cargar las listas del usuario
    class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

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

                for(int l=0; l<arrayList.size(); l++){
                    addCard();
                    tv.setText(arrayList.get(l).getTitle());
                    tv2.setText("Lista creada el "+arrayList.get(l).getDate());
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

}
