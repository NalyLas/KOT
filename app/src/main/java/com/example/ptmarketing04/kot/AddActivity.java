package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.example.ptmarketing04.kot.Adapters.ViewPagerAdapter;

public class AddActivity extends AppCompatActivity {

    private Toolbar tb;
    private ViewPager vp;
    private TabLayout tabs;
    private int tab_activa,cod,id;
    private Bundle bundle;
    private int tabColor;
    private String theme;
    static public SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Modificamos tema en función de las preferencias del usuario
        pref = getSharedPreferences("com.example.ptmarketing04.kot_preferences", MODE_PRIVATE);
        theme = pref.getString("theme_pref","OR");
        switch (theme){
            case "OR":
                setTheme(R.style.OrangeTheme);
                tabColor = getResources().getColor(R.color.deepOrangePrimary);
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                tabColor = getResources().getColor(R.color.blueGrayPrimary);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                tabColor = getResources().getColor(R.color.tealPrimary);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                tabColor = getResources().getColor(R.color.deepPurplePrimary);
                break;
        }

        setContentView(R.layout.activity_add);
        //añadimos toolbar
        tb = (Toolbar) findViewById(R.id.toolbar);

        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
            tab_activa = extras.getInt("tab_activa");
        }

        vp = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(vp);

        //añadimos tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
        tabs.setSelectedTabIndicatorColor(tabColor);
        tabs.setBackgroundColor(getResources().getColor(R.color.greyGeneral));
        tabs.setSelectedTabIndicatorHeight(15);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
     //   menu.findItem(R.id.add).setIcon(getResources().getDrawable(R.mipmap.ic_add_active));
        menu.findItem(R.id.preferencias).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.list:
                i = new Intent(this,ListActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;
            case R.id.important:
                i = new Intent(this,UrgentListActivity.class);
                i.putExtra("user",cod);
                startActivity(i);
                finish();
                return true;
            case R.id.chart:
                i = new Intent(this,SummaryActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        //Enviamos datos
        bundle=new Bundle();
        bundle.putInt("user",cod);

        //Creamos los fragment
        AddList al = new AddList();
        al.setArguments(bundle);
        AddTask at = new AddTask();
        at.setArguments(bundle);

        //Cargamos los fragment
        adapter.addFragment(al, getResources().getString(R.string.add_list));
        adapter.addFragment(at, getResources().getString(R.string.add_task));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }



}
