package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para enviar solicitudes de amistad
 */
public class SolicitudAmigoTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public SolicitudAmigoTask(ArrayList<Object> data, MainActivity mainActivity){
        this.data = data;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {
        // Leer la respuesta del servidor del flujo de entrada del socket


        return data;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {


            int exito = (int) response.get(0);

            if(exito == 0){
                Toast.makeText(mainActivity, "Solicitud de amistad enviada", Toast.LENGTH_SHORT).show();
            }
            else if(exito == 1){
                Toast.makeText(mainActivity, "No se encuentra el usuario", Toast.LENGTH_SHORT).show();
            }
            else if(exito == 2){
                Toast.makeText(mainActivity, "Ya hay una solicitud pendiente", Toast.LENGTH_SHORT).show();
            }
            else if(exito == 3){
                Toast.makeText(mainActivity, "El usuario ya es tu amigo", Toast.LENGTH_SHORT).show();
            }


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
