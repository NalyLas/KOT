package com.example.ptmarketing04.kot;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AddActivity extends AppCompatActivity {

    protected Toolbar tb;
    protected ViewPager vp;
    protected TabLayout tabs;
    protected int tab_activa;
    Bundle extras,bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //añadimos toolbar
     /*   tbt = (Toolbar) findViewById(R.id.toolbartabs);

        if(tbt != null){
            tbt.setTitle(getResources().getString(R.string.account_title));
            tbt.setSubtitle(getResources().getString(R.string.account_subtitle));
            tbt.setSubtitleTextColor(getResources().getColor(R.color.Gainsboro));
        }

        if(sp.getBoolean("isPrefMargin",false)){
            tbt.setBackgroundColor(getResources().getColor(R.color.main_orange));
        }else{
            tbt.setBackgroundColor(getResources().getColor(R.color.DeepSkyBlue));

        }

        setSupportActionBar(tbt);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        extras = getIntent().getExtras();
        if(extras!=null){
            tab_activa = extras.getInt("tab_activa");
        }


      /*  url_codificada= GetUris.getFinancial(GlobalParams.COD_USER,GlobalParams.TOKEN_USER);
        url_consulta = ServiceParams.CUSTOMER_FINANCIAL_CREDIT + url_codificada;
        devuelveJSON = new ClaseConexion();
        new Conectar().execute();*/

        vp = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(vp);

        //añadimos tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
        tabs.setBackgroundColor(getResources().getColor(R.color.blueGrayAccent));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.blueGrayPrimary));
        tabs.setSelectedTabIndicatorHeight(15);



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        //Enviamos datos
      /*  bundle=new Bundle();
        bundle.putInt("credito_actual",credito_actual);
        bundle.putInt("credito_limite",credito_limite);
        bundle.putInt("impago",impago);*/


        //Creamos los fragment
        AddList al = new AddList();
      //  al.setArguments(bundle);
        AddTask at = new AddTask();
     //   pt2.setArguments(bundle);


        //Cargamos los fragment
        adapter.addFragment(al, getResources().getString(R.string.add_list));
        adapter.addFragment(at, getResources().getString(R.string.add_task));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }
}
