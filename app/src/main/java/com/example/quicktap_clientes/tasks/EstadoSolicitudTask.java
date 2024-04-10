package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para obtener el estado de las solicitudes de amistad
 */
public class EstadoSolicitudTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public EstadoSolicitudTask(ArrayList<Object> data, MainActivity mainActivity){
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
            boolean aceptarRechazar = (boolean) response.get(1);

            if(exito){

                if(aceptarRechazar){
                    Toast.makeText(mainActivity, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mainActivity, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                Toast.makeText(mainActivity, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
