package com.example.quicktap_clientes.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para actualizar los datos del usuario
 */
public class ActualizarDatosUsuarioTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public ActualizarDatosUsuarioTask(ArrayList<Object> data, MainActivity mainActivity){
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

            boolean exito = (boolean) response.get(0);


            if(exito){

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

                String nuevoUsuario = (String) response.get(1);

                prefs.edit().putString("usuario",nuevoUsuario);
                prefs.edit().apply();

                mainActivity.usuario = nuevoUsuario;

            }
            else{
                Toast.makeText(mainActivity, "No se ha podido actualizar los datos. Compruebe los campos.", Toast.LENGTH_SHORT).show();
            }



        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
