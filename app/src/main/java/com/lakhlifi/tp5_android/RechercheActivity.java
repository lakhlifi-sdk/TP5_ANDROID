package com.lakhlifi.tp5_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakhlifi.tp5_android.Models.Etudiant;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RechercheActivity extends AppCompatActivity {

    public EditText ed_code;
    public TextView tv_resultat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
       init();
    }


    public void rechercher(View view) {

        RecherchetDataTask recherchetDataTask=new RecherchetDataTask();
        recherchetDataTask.execute();
        tv_resultat.setVisibility(View.VISIBLE);
    }

    protected  class RecherchetDataTask extends AsyncTask<String, Void, Boolean> {

        private  boolean isFound=false;
        String response = "";
        @Override
        protected void onPostExecute(Boolean aBoolean) {

            JSONArray ja= null;
            try {
                ja = new JSONArray(response);
                if(ja.length()>0){

                    JSONObject jo= (JSONObject) ja.get(0);
                    Etudiant etudiant = new Etudiant(Integer.valueOf(jo.get("code").toString()), jo.get("nom").toString(), jo.get("prenom").toString(), "");
                    tv_resultat.setText(etudiant.toString());
                }
                else {
                    tv_resultat.setText("Aucun étudiant est trouvé avec ce code");
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            HashMap<String,String> values=new HashMap<>();
            values.put("col1",ed_code.getText().toString());


            try {
                URL url = new URL("http://192.168.0.115/Etudiant_scripts/Recherche.php");
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

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TP5",e.getMessage());
            }

            return null;
        }


    }

    private void init() {
        setTitle("Recherche");
        ed_code=findViewById(R.id.editText_code);
        tv_resultat=findViewById(R.id.textView_resultat);

    }
}
