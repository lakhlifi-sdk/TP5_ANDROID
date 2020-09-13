package com.lakhlifi.tp5_android;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

import com.lakhlifi.tp5_android.Models.Etudiant;

public class MainActivity extends AppCompatActivity {

    public Button btn_new,btn_search,btn_remove,btn_nouveau;
    public static ListView listview;
    public static ArrayAdapter<Etudiant> adapter;
    public static List<Etudiant> etudiants;
    public int selectedCode=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btn_remove=findViewById(R.id.button_supprimer);
        listview=findViewById(R.id.listview);
        btn_nouveau=(Button)findViewById(R.id.button_nouveau);

        btn_nouveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,AjouterEtudiantActivity.class));
            }
        });



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btn_remove.setEnabled(true);
                btn_remove.setBackgroundColor(Color.RED);
                selectedCode=etudiants.get(position).getCode();
            }
        });


        etudiants=new ArrayList<Etudiant>();
        adapter=new ArrayAdapter<Etudiant>(this,android.R.layout.simple_list_item_1,etudiants);
        listview.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();
    }

    public static void initListView(){
        etudiants.clear();
        AccesHTTP fetchingdata=new AccesHTTP();
        fetchingdata.execute();

    }
   /* public void toAjouter(View view) {
        Intent i=new Intent(MainActivity.this,AjouterEtudiantActivity.class);
        startActivity(i);
    }*/

    public void supprimer(View view) {
        if(selectedCode>0){
            DeleteDataTask deleteDataTask=new DeleteDataTask();
            deleteDataTask.execute();
        }
    }

    public void toRecherche(View view) {
        startActivity(new Intent(this,RechercheActivity.class));
    }

    private class DeleteDataTask extends AsyncTask<String, Void, Boolean> {

        private boolean isDeleted=false;
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(isDeleted){
                Toast.makeText(MainActivity.this, "Etudiant est bien supprimé", Toast.LENGTH_SHORT).show();
                MainActivity.initListView();
                selectedCode=-1;
            }
            else {
                Toast.makeText(MainActivity.this, "Etudiant n'est pas supprimé, un problème quelconque", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            //construire l'objet values qui contient le code de l étudiant à supprimer
            HashMap<String,String> values=new HashMap<>();
            values.put("col1",String.valueOf(selectedCode));

            try {
                URL url = new URL("http://192.168.0.115/Etudiant_scripts/Suppression.php");
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
                            isDeleted=true;
                        }
                        else {
                            isDeleted=false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("TP 5", "Etudiant supprimé : "+resp);



                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TP5",e.getMessage());
            }

            return null;
        }
    }
}
