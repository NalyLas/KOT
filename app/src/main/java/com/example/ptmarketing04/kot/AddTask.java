package com.example.ptmarketing04.kot;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.example.ptmarketing04.kot.Objects.GeneralTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    private GeneralTask task;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> arrayTask;
    private ArrayList<HashMap<String, String>> allList;
    private int cod,listas,idt,idl;
    private String date,title,dateEnd, details;

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

        addTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new GetTotalTask().execute();
                getParams();


                new AddNewTask().execute();

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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date = df.format(c.getTime());

        details = etDetail.getText().toString();

        if(etDate.getText().toString().equals("") || etDate.getText() == null){
            Snackbar.make(getView(), "Elija una fecha de finalización para la tarea", Snackbar.LENGTH_LONG).show();
        }else{
            dateEnd = etDate.getText().toString();
        }
        if(!etTitle.getText().toString().equals(" ") && !etTitle.getText().equals(null)){
            title = etTitle.getText().toString();
        }else{
            Snackbar.make(getView(), "Debe introducir un titulo para la tarea", Snackbar.LENGTH_LONG).show();
        }

        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getTitle().equals(spinner.getSelectedItem())){
                idl = arrayList.get(i).getId();
                break;
            }
        }


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

    //     Task para cargar las tareas
    class GetTotalTask extends AsyncTask<String, String, JSONArray> {
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
                arrayTask =new ArrayList<GeneralTask>();
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

                        arrayTask.add(task);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Buscar la manera de que añada ids aunque no existan tareas
                int aux;
                aux = arrayList.size()-1;
                idl = arrayTask.get(aux).getId_task();
                idl = idl+1;

            } else {
                idl = 1;
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //
    //  Formato para añadir más de un item a la vez
    //  INSERT INTO `Listas` (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES ('2', 'Lista numero 2', '1', '02/05/2017'), ('6', 'Lista numero 2', '1', '02/05/2017');
    //

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
                parametrosPost.put("ins_sql", "Insert into Tareas (`ID_tarea`, `Titulo`, `Fech_inicio`, `Fecha_fin`, `Finalizada`, `Urgente`, `Lista`) VALUES ("+ idt +",'"+ title +"','"+ date+"','"+ dateEnd +"',0,0,"+ idl +")");

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){

                    Snackbar.make(getView(), "añadido", Snackbar.LENGTH_LONG).show();

                }else{
                    Snackbar.make(getView(), "un carajo", Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para agregar contenido a las tareas
   /* class AddContendTask extends AsyncTask<String, String, JSONObject> {
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
                parametrosPost.put("ins_sql", "Insert into Tareas (`ID_tarea`, `Titulo`, `Fech_inicio`, `Fecha_fin`, `Finalizada`, `Urgente`, `Lista`) VALUES ("+ id +",'"+ title +"',"+ cod +",'"+ date +"')");

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

    }*/




}
