package com.lakhlifi.tp5_android;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
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

public class AccesHTTP  extends AsyncTask<String, Void, String> {
    String receivedData="";
    Etudiant[] etudiants;
    public AccesHTTP(){
    }

    protected void onPreExecute() {
        Log.d("onPreExecute","--------");
    }

    protected void onPostExecute(String result){

        //Log.d("premier","--------"+result);
        JSONArray ja= null;
        try {
            ja = new JSONArray(receivedData);
            etudiants=new Etudiant[ja.length()];
            for(int i=0;i<ja.length();i++){
                JSONObject jo= (JSONObject) ja.get(i);
                //Log.i("TP5",jo.get("code")+" : "+jo.get("nom")+" "+jo.get("prenom"));
                Etudiant etudiant = new Etudiant(Integer.valueOf(jo.get("code").toString()),jo.get("nom").toString(),jo.get("prenom").toString(),"");
                MainActivity.etudiants.add(etudiant);
            }
            MainActivity.listview.setAdapter(MainActivity.adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.i("TP5",receivedData);
    }


    @Override
    protected String doInBackground(String... strings) {
        String result= "";
        //String nom=strings[0];
        //String serverADDR = "https://github.com/typicode/demo/blob/master/db.json";
        String serverADDR = "http://192.168.0.115/Etudiant_scripts/Affichage.php";

        try {
            URL url=new URL(serverADDR);
            HttpURLConnection http=(HttpURLConnection)url.openConnection();
            InputStream inputStream=http.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while (line!=null){
                line=bufferedReader.readLine();
                receivedData=receivedData+line;
            }

            //Log.i("TP5",receivedData);



        } catch (IOException e) {
            result=e.getMessage();
            result=result+"22222";

            Log.i("TP5",result);
        }
        return result;

    }

}
