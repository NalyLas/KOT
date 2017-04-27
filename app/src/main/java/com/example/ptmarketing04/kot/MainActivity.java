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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<HashMap<String, String>> allList;
    private int cod;

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

        Log.e("main","asfgdrhst");


        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }

        new ListTask().execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.preferencias:
                Intent i = new Intent(this,Preferences.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addChild() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.list_card;

        CardView cardView = (CardView)inflater.inflate(id,null,false);
        tv = (TextView)cardView.findViewById(R.id.list_title);

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
                    addChild();
                    tv.setText(arrayList.get(l).getTitle());
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

}
