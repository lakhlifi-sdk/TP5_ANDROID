package com.lakhlifi.tp5_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AjouterEtudiantActivity extends AppCompatActivity {

    public EditText ed_nom,ed_prenom,ed_classe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_etudiant);
        setTitle("Ajouter un nouvel étudiant");

        ed_nom=findViewById(R.id.editText_nom);
        ed_prenom=findViewById(R.id.editText_prenom);
        ed_classe=findViewById(R.id.editText_classe);

        String nom=ed_nom.getText().toString();
        String prenom=ed_prenom.getText().toString();
        String classe=ed_classe.getText().toString();



    }

    //cette fonction transforme le Map de données au format url(String)
    public static String getPostData(HashMap<String, String> values) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (first)
                first = false;
            else
                builder.append("&");
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {}
        }
        return builder.toString();
    }

    public void ajouter(View view) {
        Log.i("TP 5 :","sending insert request..");
        InsertDataTask insertDataTask=new InsertDataTask();
        insertDataTask.execute();
    }



    protected  class InsertDataTask extends AsyncTask<String, Void, Boolean>{

        private  boolean isInserted=false;
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(isInserted){
                Toast.makeText(AjouterEtudiantActivity.this, "Etudiant est bien éjouté", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AjouterEtudiantActivity.this,MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(AjouterEtudiantActivity.this, "Etudiant n'est éjouté, un problème quelconque", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            //construire l'objet values qui contient les valeurs à être envoyées au serveur avec leur clés col2 (nom),col3 (prénom),et col4(classe)
            HashMap<String,String> values=new HashMap<>();
            values.put("col2",ed_nom.getText().toString());
            values.put("col3",ed_prenom.getText().toString());
            values.put("col4",ed_classe.getText().toString());


            try {
                URL url = new URL("http://192.168.0.115/Etudiant_scripts/Ajout.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                if (values != null) {
                    OutputStream os = conn.getOutputStream();
                    OutputStreamWriter osWriter = new OutputStreamWriter(os, "UTF-8");
                    BufferedWriter writer = new BufferedWriter(osWriter);
                    writer.write(AjouterEtudiantActivity.getPostData(values));

                    writer.flush();
                    writer.close();
                    os.close();
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {


                    InputStream is = conn.getInputStream();
                    InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
                    BufferedReader reader = new BufferedReader(isReader);

                    String resp = "";
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        resp += line;
                    }

                    try {
                        JSONObject jo_status= new JSONObject(resp);
                        String status=jo_status.get("message").toString();
                        if(status.compareTo("OK")==0){
                            isInserted=true;
                        }
                        else {
                            isInserted=false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("resp", "doInBackground: "+resp);



                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TP5",e.getMessage());
            }



            return null;
        }


    }



}
