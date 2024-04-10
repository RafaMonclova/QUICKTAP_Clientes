package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.adapters.ProductAdapter;

import java.util.ArrayList;

/**
 * AsyncTask para cargar los amigos del usuario
 */
public class AmigosTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<ArrayList<Object>> amigos;


    public AmigosTask(ArrayList<ArrayList<Object>> amigos, MainActivity mainActivity){
        this.amigos = amigos;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<ArrayList<Object>> doInBackground(Void... voids) {


        return amigos;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {

            mainActivity.friendsFragment.setAmigos(response);


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
