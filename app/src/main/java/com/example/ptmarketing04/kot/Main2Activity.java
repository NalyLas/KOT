package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private RecyclerView recView;
    private ArrayList<GeneralList> datos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        datos = new ArrayList<GeneralList>();
        for(int i=0; i<550; i++)
            datos.add(new GeneralList(1, i, "Título " +i, "15/15/15"));

        recView = (RecyclerView) findViewById(R.id.recView);
        //Esto no es obligatorio pero si recomendable si siempre va a tener un nº de elementos fijo
        recView.setHasFixedSize(true);

        final GeneralListAdapter adaptador = new GeneralListAdapter(datos);

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si no consigues capturar el botón prueba al pulser el item abrir una nueva venta
                //que contenga lo datos de la lista y ahi le metes un flotente de editar y un boton
                // para eliminar la llita con todas sus tareas
                Intent i = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(i);
                Log.i("DemoRecView", "Pulsado el elemento " + recView.getChildPosition(v));
            }
        });



        recView.setAdapter(adaptador);

       // recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recView.setLayoutManager(new GridLayoutManager(this,3));

        recView.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));

        //   recView.addItemDecoration(
        //           new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));

        recView.setItemAnimator(new DefaultItemAnimator());
    }
}
