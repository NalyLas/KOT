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
import java.util.List;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AddTask extends Fragment {
    private Spinner spinner;
    private EditText etTitle, etDetail, etDate;
    private Button addTask;
    private  ImageButton addCheck, addImg, addPdf, addDate;
    private CheckBox cbUrgent;

    private JSONArray jSONArray;
    private JSONObject jsonObject;
    private Connection conn;
    private GeneralList list;
    private GeneralTask task;
    private GeneralContent content;
    private ArrayList<GeneralList> arrayList;
    private ArrayList<GeneralTask> arrayTask;
    private ArrayList<GeneralContent> arrayContent;
    private int cod,urgent,idt,idl,idc,tipe;
    private String date,title,dateEnd, details;

    public AddTask() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new Connection();


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle = (EditText) view.findViewById(R.id.etTitleTask);
        etDetail = (EditText) view.findViewById(R.id.etDetailTask);
        etDate = (EditText) view.findViewById(R.id.etDate);
        spinner = (Spinner) view.findViewById(R.id.spinner_list);
        cbUrgent = (CheckBox) view.findViewById(R.id.cbUrgent);

        addTask = (Button) view.findViewById(R.id.btAddTask);
        addCheck = (ImageButton) view.findViewById(R.id.btCheck);
        addImg = (ImageButton) view.findViewById(R.id.btImage);
        addPdf = (ImageButton) view.findViewById(R.id.btPdf);
        addDate = (ImageButton) view.findViewById(R.id.btDate);
        new ListTask().execute();


        addDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //alert
                final Calendar c = Calendar.getInstance();
                final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

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
                getParams();

                new AddNewTask().execute();
                //Recargamos tareas
                new GetTotalTask().execute();
                new GetContentTask().execute();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        new ListTask().execute();
        new GetTotalTask().execute();
        new GetContentTask().execute();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_add_task, container, false);
    }

    public void getParams(){

        //calculamos la fecha de inicio de la tarea
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date = df.format(c.getTime());

        //Comprobamos la fecha limite de la tarea
        if(etDate.getText().toString().equals("") || etDate.getText() == null){
            Snackbar.make(getView(), "Elija una fecha de finalización para la tarea", Snackbar.LENGTH_LONG).show();
        }else{
            dateEnd = etDate.getText().toString();
        }

        //Comprobamos el titulo de la tarea
        if(!etTitle.getText().toString().equals(" ") && !etTitle.getText().equals(null)){
            title = etTitle.getText().toString();
        }else{
            Snackbar.make(getView(), "Debe introducir un titulo para la tarea", Snackbar.LENGTH_LONG).show();
        }

        //obtenemos el id de la lista a la que pertenece
        for(int i=0;i<arrayList.size();i++){
            if(arrayList.get(i).getTitle().equals(spinner.getSelectedItem())){
                idl = arrayList.get(i).getId();
                break;
            }
        }

        //calculamos el id de la tarea
        if(arrayTask.size()>0){
            idt = arrayTask.get(arrayTask.size()-1).getId_task()+1;
        }else{
            idt=0;
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
                parametrosPost.put("ins_sql", "Select * from LIST where User ="+cod);

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
                        list.setId_user(jsonObject.getInt("user"));
                        list.setTitle(jsonObject.getString("Title"));
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
                arrayTask =new ArrayList<GeneralTask>();
                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        task = new GeneralTask();
                        task.setId_task(jsonObject.getInt("ID_task"));
                        task.setTitle(jsonObject.getString("Title"));
                        task.setStart_date(jsonObject.getString("Start_date"));
                        task.setEnd_date(jsonObject.getString("End_date"));
                        task.setFinished(jsonObject.getInt("Finished"));
                        task.setUrgent(jsonObject.getInt("Urgent"));
                        task.setId_list(jsonObject.getInt("List"));

                        arrayTask.add(task);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //     Task para cargar el contenido de las tareas
    class GetContentTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "Select * from CONTENT");

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
                arrayContent =new ArrayList<GeneralContent>();
                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        content = new GeneralContent();
                        content.setId(jsonObject.getInt("ID_content"));
                        content.setTask(jsonObject.getInt("Task"));
                        content.setTipe(jsonObject.getInt("Type"));
                        content.setDetail(jsonObject.getString("Info"));

                        arrayContent.add(content);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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
                parametrosPost.put("ins_sql", "Insert into TASK (`ID_task`, `Title`, `Start_date`, `End_date`, `Finished`, `Urgent`, `List`, `User`) VALUES ("+ idt +",'"+ title +"','"+ date+"','"+ dateEnd +"',0,"+ urgent +","+ idl +","+ cod + ")");

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

                    Snackbar.make(getView(), "añadido", Snackbar.LENGTH_LONG).show();
                    new AddContentTask().execute();

                }else{
                    Snackbar.make(getView(), "un carajo", Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //task para agregar contenido a las tareas
    class AddContentTask extends AsyncTask<String, String, JSONObject> {
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
                parametrosPost.put("ins_sql", "INSERT INTO `CONTENT`(`ID_content`, `Type`, `Info`, `Task`) VALUES ("+ idc +","+ tipe +","+ details +",'"+ idt +"')");
                //
                //  Formato para añadir más de un item a la vez
                //  INSERT INTO `Listas` (`ID_lista`, `Titulo`, `user`, `Fecha`) VALUES ('2', 'Lista numero 2', '1', '02/05/2017'), ('6', 'Lista numero 2', '1', '02/05/2017');
                //

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
