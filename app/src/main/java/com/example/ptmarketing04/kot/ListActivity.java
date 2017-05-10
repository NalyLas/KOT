package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ptmarketing04.kot.Adapters.ViewPagerAdapter;

public class ListActivity extends AppCompatActivity {

    protected Toolbar tb;
    protected ViewPager vp;
    protected TabLayout tabs;
    protected int tab_activa,cod,id;
    Bundle bundle;
    protected String theme;
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
                break;
            case "GR":
                setTheme(R.style.GrayTheme);
                break;
            case "TL":
                setTheme(R.style.TealTheme);
                break;
            case "PR":
                setTheme(R.style.DeepPurpleTheme);
                break;
        }


        setContentView(R.layout.activity_list);

        //añadimos toolbar
        tb = (Toolbar) findViewById(R.id.toolbar);

        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            cod = extras.getInt("user");
        }


        vp = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(vp);

        //añadimos tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
        tabs.setBackgroundColor(getResources().getColor(R.color.deepOrangePrimary));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.specialWhite));
        tabs.setSelectedTabIndicatorHeight(15);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.preferencias:
                i = new Intent(this,Preferences.class);
                startActivity(i);
                return true;
            case R.id.task:
                //      i = new Intent(this,ListActivity.class);
                //    i.putExtra("user",cod);
                //    startActivity(i);
                return true;
            case R.id.add:
                i = new Intent(this,AddActivity.class);
                i.putExtra("user",cod);
                i.putExtra("tab_activa",0);
                startActivity(i);
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
        AllListsActivity al = new AllListsActivity();
        al.setArguments(bundle);
        AllTaskActivity at = new AllTaskActivity();
        at.setArguments(bundle);


        //Cargamos los fragment
        adapter.addFragment(al, getResources().getString(R.string.list_title));
        adapter.addFragment(at, getResources().getString(R.string.all_task_title));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }

}