package com.example.ptmarketing04.kot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SigninActivity extends AppCompatActivity {

    private EditText etName,etEmail,etPass,etPass2;
    private ArrayList<String> users;
    private String url = "http://iesayala.ddns.net/natalia/php.php";
    private String url_dml = "http://iesayala.ddns.net/natalia/prueba.php";
    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private Connection conn;
    private User user;
    private ArrayList<User> arrayUsers;
    private ArrayList<HashMap<String, String>> userList;
    private String name,mail,pass;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etMail);
        etPass = (EditText)findViewById(R.id.etPass);
        etPass2 = (EditText)findViewById(R.id.etPass2);

        url = "http://iesayala.ddns.net/natalia/php.php";
        url_dml = "http://iesayala.ddns.net/natalia/prueba.php";

        conn = new Connection();
        new LoginTask().execute();
    }

    public void  sign(View view){
        boolean exist = false;

        if(etName.getText().toString().equals("") || etEmail.getText().toString().equals("") || etPass.getText().toString().equals("")) {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.emptyFields), Snackbar.LENGTH_LONG).show();
            exist = true;

        }else if(etName.getText().equals(null) || etEmail.getText().equals(null) || etPass.getText().equals(null) ){
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.emptyFields), Snackbar.LENGTH_LONG).show();
            exist = true;

        }else if(!etPass.getText().toString().equals(etPass2.getText().toString())){
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.wrongPass), Snackbar.LENGTH_LONG).show();
            exist = true;
        }else{
            name = etName.getText().toString();
            mail = etEmail.getText().toString();
            pass = etPass.getText().toString();

            for(int i=0;i<arrayUsers.size();i++){
                if(mail.equals(arrayUsers.get(i).getEmail())){
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.emailExist), Snackbar.LENGTH_LONG).show();
                    exist = true;
                    break;
                }
            }

        }

        if(!exist) {
            findId();
            new SigninTask().execute();
        }

    }

    public void findId(){
        int aux;
        aux = arrayUsers.size()-1;
        id = arrayUsers.get(aux).getId();
        id = id+1;
    }



    //  Task para comprobar conexcion de usuario
    class LoginTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SigninActivity.this);
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

    //     Task para registrar un nuevo usuario
    class SigninTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SigninActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql",  "INSERT INTO `Usuarios`(`ID_user`, `Name`, `Email`, `Password`) VALUES ("+id+",'" + name + "','" + mail + "','" + pass + "')");

                jsonObject = conn.sendDMLRequest(url_dml, parametrosPost);

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
                    AlertDialog.Builder builders = new AlertDialog.Builder(SigninActivity.this);
                    builders.setMessage(R.string.signin);
                    builders.setPositiveButton(getResources().getString(R.string.acept),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Volvemos al login
                                    Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                                    intent.putExtra("email",mail);
                                    startActivity(intent);
                                }
                            });
                    builders.create();
                    builders.show();


                }else{
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG).show();
            }

        }

    }

}
