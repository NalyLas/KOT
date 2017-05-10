package com.example.ptmarketing04.kot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ptmarketing04.kot.Objects.GeneralList;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 03/05/2017.
 */

public class AllTaskActivity extends Fragment {

    protected RecyclerView recView;

    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private String url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
    private JSONArray jSONArray;
    private Connection conn;
    private GeneralList list;
    private ArrayList<GeneralList> arrayList;
    private int cod;

    public AllTaskActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recView = (RecyclerView) view.findViewById(R.id.recView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cod = getArguments().getInt("user");
       // new ListTask().execute();

        return inflater.inflate(R.layout.layout_all_lists, container, false);
    }


    //     Task para cargar las listas del usuario
  /*  class ListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        private int count=0;

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
                        list.setDate(jsonObject.getString("Fecha"));
                        arrayList.add(list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                //Esto no es obligatorio pero si recomendable si siempre va a tener un nº de elementos fijo
                recView.setHasFixedSize(true);

                final GeneralListAdapter adaptador = new GeneralListAdapter(arrayList);

                //   adaptador.totalTask(datos.size());

                adaptador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), MainListActivity.class);
                        i.putExtra("lista",arrayList.get(recView.getChildPosition(v)).getId());
                        i.putExtra("title",arrayList.get(recView.getChildPosition(v)).getTitle());
                        i.putExtra("user",cod);
                        startActivity(i);
                    }
                });


                recView.setAdapter(adaptador);

                recView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                //recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                // recView.setLayoutManager(new GridLayoutManager(this,3));

                recView.addItemDecoration(
                        new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));

                //   recView.addItemDecoration(
                //           new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));

                recView.setItemAnimator(new DefaultItemAnimator());

            } else {
                Snackbar.make(getView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }
    */

}
