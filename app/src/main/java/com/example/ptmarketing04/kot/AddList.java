package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AddList extends Fragment {

    protected EditText etName;
    protected Button addlist;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private String url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<HashMap<String, String>> allList;
    private int cod,listas,id;
    private String date,title;


    public AddList() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = "http://iesayala.ddns.net/natalia/php.php";
        url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
        conn = new Connection();
        new ListTask().execute();
    }


    public void getParams(){
        findId();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date = df.format(c.getTime());
        if(!etName.getText().toString().equals(" ") && !etName.getText().equals(null)){
            title = etName.getText().toString();
        }else{
            Snackbar.make(getView(), "Debe introducir un titulo para la lista", Snackbar.LENGTH_LONG).show();
        }
    }

    public void findId(){
        int aux;
        aux = arrayList.size()-1;
        id = arrayList.get(aux).getId();
        id = id+1;
    }

    public boolean isExist(){
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getTitle().equals(title)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = (EditText) view.findViewById(R.id.etListName);
        addlist = (Button) view.findViewById(R.id.btAddList);

        addlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getParams();
                if(isExist()){
                    Snackbar.make(getView(), "ya tiene una trea con ese nombre", Snackbar.LENGTH_LONG).show();
                }else{
                    new AddListTask().execute();
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        listas = getArguments().getInt("listas");
        return inflater.inflate(R.layout.layout_add_list, container, false);
    }




    //     Task para cargar las listas del usuario
    class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
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
            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }


    //     Task para agregar una lista al usuario
    class AddListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        int add;


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Insert into Listas (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES ("+id+",'"+title+"',"+ cod +",'"+date+"')");
                //Insert into Listas (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES (15,'title',1,'15/04/2017');

                jSONArray = conn.sendRequest(url_dml, parametrosPost);

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
                try {
                    add = json.getInt(Integer.parseInt("added"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){
                    Snackbar.make(getView(), "añadido", Snackbar.LENGTH_LONG).show();


                }else{
                    Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }
        }

    }



}
