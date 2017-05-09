package com.example.ptmarketing04.kot;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.ptmarketing04.kot.Objects.GeneralList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AddTask extends Fragment {
    protected Spinner spinner;
    protected EditText etTitle, etDetail, etDate;
    protected Button addTask;
    protected  ImageButton addCheck, addImg, addPdf, addDate;

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

    public AddTask() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = "http://iesayala.ddns.net/natalia/php.php";
        url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
        conn = new Connection();


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle = (EditText) view.findViewById(R.id.etTitleTask);
        etDetail = (EditText) view.findViewById(R.id.etDetailTask);
        etDate = (EditText) view.findViewById(R.id.etDate);
        spinner = (Spinner) view.findViewById(R.id.spinner_list);

        addTask = (Button) view.findViewById(R.id.btAddTask);
        addCheck = (ImageButton) view.findViewById(R.id.btCheck);
        addImg = (ImageButton) view.findViewById(R.id.btImage);
        addPdf = (ImageButton) view.findViewById(R.id.btPdf);
        addDate = (ImageButton) view.findViewById(R.id.btDate);



        addDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //alert
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        new ListTask().execute();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_add_task, container, false);
    }

    public void getParams(){

    }

    //
    //  Formato para añadir más de un item a la vez
    //  INSERT INTO `Listas` (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES ('2', 'Lista numero 2', '1', '02/05/2017'), ('6', 'Lista numero 2', '1', '02/05/2017');
    //

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
                parametrosPost.put("ins_sql", "Select * from Listas where user ="+cod);

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

                List<String> list = new ArrayList<String>();
                for (int i = 0; i <arrayList.size() ; i++) {
                    list.add(arrayList.get(i).getTitle());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    //task para agregar tareas

    class AddNewTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Insert into Listas (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES ("+ id +",'"+ title +"',"+ cod +",'"+ date +"')");

                jsonObject = conn.sendDMLRequest(url_dml, parametrosPost);

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

                    Log.e("añadido??", add+"");
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
