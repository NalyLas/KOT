package com.example.ptmarketing04.kot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    protected LinearLayout llist;
    protected TextView tv;
    protected Toolbar tb;

    static public SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tb = (Toolbar) findViewById(R.id.toolbar);
        llist = (LinearLayout)findViewById(R.id.linerat_list);

        pref = getSharedPreferences("com.example.ptmarketing04.kot_preferences", MODE_PRIVATE);



        if(tb != null){
            tb.setTitle("ninini");
            setSupportActionBar(tb);
        }

        for(int i=0;i<5;i++){
//            tv.setText("Lista nº "+i);
            addChild();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.preferencias:
                Intent i = new Intent(this,Preferences.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addChild()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.list_card;

        ///RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
        CardView cardView = (CardView)inflater.inflate(id,null,false);
        tv = (TextView)cardView.findViewById(R.id.list_title);

       // TextView textView = (TextView) relativeLayout.findViewById(R.id.textViewDate);
       // textView.setText(String.valueOf(System.currentTimeMillis()));

        llist.addView(cardView);

    }
}
