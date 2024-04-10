package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.activities.MapActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para cargar los datos del usuario en el ProfileFragment
 */
public class DatosUsuarioTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public DatosUsuarioTask(ArrayList<Object> data, MainActivity mainActivity){
        this.data = data;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {

            return data;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {

            mainActivity.profileFragment.setDatosUsuario(response);


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
