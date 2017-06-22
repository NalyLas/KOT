package com.example.ptmarketing04.kot;

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
import android.widget.TextView;

import com.example.ptmarketing04.kot.Objects.ChartTask;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class SummaryMonth extends Fragment {

    private PieChart dayChart;
    private TextView tvEmpty;

    private JSONArray jSONArray;
    private JSONObject jsonObject;
    private Connection conn;
    private ChartTask chartTask;
    private ArrayList<ChartTask> chartList,chartList2;
    private int cod,tu = 0,tnu = 0, colorU,colorN;
    private String first, last;


    public SummaryMonth() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new Connection();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dayChart = (PieChart) view.findViewById(R.id.dayChart);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        tvEmpty.setText(getResources().getString(R.string.empty_month));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
        colorN = getArguments().getInt("colorN");
        colorU = getArguments().getInt("colorU");
        getParams();

        return inflater.inflate(R.layout.layout_summary_day, container, false);
    }

    public void getParams(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        first = df.format(c.getTime());

        int ld = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, ld);
        last = df.format(c.getTime());

        new GetChartNormalTask().execute();

    }

    //  Task para cargar el numero de tareas no urgentes del día
    class GetChartNormalTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "SELECT COUNT(*) AS total_number FROM `TASK` WHERE `User`= "+cod+" AND `Urgent` = 0 AND `End_date` BETWEEN '"+ first +"' AND '"+ last +"'");

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
                int aux = 0;
                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        aux = jsonObject.getInt("total_number");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(aux == 0){
                    tnu = 0;
                }else{
                    tnu = aux;
                }

                new GetChartUrgentTask().execute();

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    //  Task para cargar el numero de tareas no urgentes del día
    class GetChartUrgentTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "SELECT COUNT(*) AS total_number FROM `TASK` WHERE `User`= "+cod+" AND `Urgent` = 1 AND `End_date` BETWEEN '"+ first +"' AND '"+ last +"'");

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
                int aux = 0;
                for (int i = 0; i < json.length(); i++) {
                    try {

                        JSONObject jsonObject = json.getJSONObject(i);
                        aux = jsonObject.getInt("total_number");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(aux == 0){
                    tu = 0;
                }else{
                    tu = aux;
                }

                if(tu == 0 && tnu == 0){
                    dayChart.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }else{
                    //Ocultamos pantalla vacia y m ostramos grafico
                    dayChart.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);

                    //Atributos del diagrama
                    dayChart.setHoleRadius(40f);
                    dayChart.setDrawYValues(true);
                    dayChart.setDrawXValues(true);
                    dayChart.setRotationEnabled(true);
                    dayChart.animateXY(1500, 1500);

                    //creamos una lista para los valores
                    ArrayList<Entry> valsY = new ArrayList<Entry>();
                    valsY.add(new Entry(tnu,0));
                    valsY.add(new Entry(tu,1));

                    //Creamos una lista para los elementos
                    ArrayList<String> valsX = new ArrayList<String>();
                    valsX.add(getResources().getString(R.string.val_urgent));
                    valsX.add(getResources().getString(R.string.val_no_urgent));

                    //Colores
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    colors.add(colorU);
                    colors.add(colorN);

                    //seteamos los valores de Y y los colores
                    PieDataSet set1 = new PieDataSet(valsY, "Resultados");
                    set1.setSliceSpace(2f);
                    set1.setColors(colors);

                    //seteamos los valores de X
                    PieData data = new PieData(valsX, set1);
                    dayChart.setData(data);
                    dayChart.highlightValues(null);
                    dayChart.invalidate();

                    //Ocutar descripcion
                    dayChart.setDescription("");
                    //Ocultar leyenda
                    dayChart.setDrawLegend(false);
                }



            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }


}
