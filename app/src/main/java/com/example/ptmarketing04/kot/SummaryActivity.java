package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ptmarketing04.kot.Adapters.ViewPagerAdapter;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private Toolbar tb;
    private ViewPager vp;
    private TabLayout tabs;
    private int tab_activa,cod;
    private Bundle bundle;
    private  int tabColor;
    private ArrayList<Integer> colors = new ArrayList<Integer>();
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
                colors.add(getResources().getColor(R.color.deepOrangePrimary));
                colors.add(getResources().getColor(R.color.deepOrangeAccent));
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                colors.add(getResources().getColor(R.color.blueGrayPrimary));
                colors.add(getResources().getColor(R.color.blueGrayAccent));
                tabColor = getResources().getColor(R.color.blueGrayPrimary);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                colors.add(getResources().getColor(R.color.tealPrimary));
                colors.add(getResources().getColor(R.color.tealAccent));
                tabColor = getResources().getColor(R.color.tealPrimary);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                colors.add(getResources().getColor(R.color.deepPurplePrimary));
                colors.add(getResources().getColor(R.color.deepPurpleAccent));
                tabColor = getResources().getColor(R.color.deepPurplePrimary);
                break;
        }

        setContentView(R.layout.activity_summary);

        tb = (Toolbar) findViewById(R.id.toolbar);
        vp = (ViewPager) findViewById(R.id.viewpager);
        tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.setupWithViewPager(vp);
        tabs.setBackgroundColor(getResources().getColor(R.color.greyGeneral));
        tabs.setSelectedTabIndicatorColor(tabColor);
        tabs.setSelectedTabIndicatorHeight(15);

        if(tb != null){
            tb.setTitle(getResources().getString(R.string.summaries));
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
            tab_activa = extras.getInt("tab_activa");
        }

        setupViewPager(vp);
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
            case R.id.add:
                i = new Intent(this,AddActivity.class);
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
        bundle.putInt("colorU",colors.get(1));
        bundle.putInt("colorN",colors.get(0));

        //Creamos los fragment
        SummaryDay sd = new SummaryDay();
        sd.setArguments(bundle);
        SummaryWeek sw = new SummaryWeek();
        sw.setArguments(bundle);

        //Cargamos los fragment
        adapter.addFragment(sd, getResources().getString(R.string.today));
        adapter.addFragment(sw, getResources().getString(R.string.week));

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }
}
