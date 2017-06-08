package com.example.ptmarketing04.kot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ptmarketing04.kot.Objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etMail, etPass;
    private Button bt1,bt2;


    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private JSONArray jSONArray;
    private Connection conn;
    private User user;
    private ArrayList<User> arrayUsers;
    private ArrayList<HashMap<String, String>> userList;
    private String theme;
    private int cod;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    //No funciona bien el logueo automatico


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.OrangeTheme);
        setContentView(R.layout.activity_login);

        etMail = (EditText)findViewById(R.id.etMail);
        etPass = (EditText)findViewById(R.id.etPass);
        bt1 = (Button)findViewById(R.id.btLogin);
        bt2 = (Button)findViewById(R.id.btSignin);

       // etMail.setText("natalia@gmail.com");
      //  etPass.setText("123456");

        url = "http://iesayala.ddns.net/natalia/php.php";
        conn = new Connection();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            etMail.setText(extras.getString("email"));
        }
        pref = getSharedPreferences("com.example.ptmarketing04.kot_preferences", MODE_PRIVATE);

        //Comprobamos si el usuario estaba previamente logueado
        //si estaba logueado lo enviamos directamente a la pantalla principal
        if(pref.getBoolean("pre_login",false)){
            etMail.setText(pref.getString("user",""));
            etPass.setText(pref.getString("pass",""));
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user",pref.getInt("cod",0));
            startActivity(intent);
            finish();
        }else{
            new LoginTask().execute();
        }

    }

    public void logUser(View view){
        String email = etMail.getText().toString();
        String pass = etPass.getText().toString();
        for(int i=0;i<arrayUsers.size();i++){
            if(email.equals(arrayUsers.get(i).getEmail()) && pass.equals(arrayUsers.get(i).getPass())){
                cod = arrayUsers.get(i).getId();

                //guardamos preferencias para futuros logueos
                editor = pref.edit();
                editor.putBoolean("pre_login",true);
                editor.putString("user",email);
                editor.putString("pass",pass);
                editor.putInt("cod",cod);
                editor.commit();

                //abrimos MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user",cod);
                startActivity(intent);
                finish();

                break;
            }else{
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.wrongUser), Snackbar.LENGTH_LONG).show();
            }
        }

    }

    public void signIn(View view){
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }

    //      Task para comprobar conexcion de usuario
    class LoginTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "Select * from Usuarios");

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
                arrayUsers =new ArrayList<User>();
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        user = new User();
                        user.setId(jsonObject.getInt("ID_user"));
                        user.setName(jsonObject.getString("Name"));
                        user.setEmail(jsonObject.getString("Email"));
                        user.setPass(jsonObject.getString("Password"));
                        user.setTheme(jsonObject.getInt("Tema"));
                        arrayUsers.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
