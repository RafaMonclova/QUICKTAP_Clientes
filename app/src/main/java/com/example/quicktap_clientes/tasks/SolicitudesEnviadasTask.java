package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.fragments.DialogSolicitudesEnviadas;

import java.util.ArrayList;

/**
 * AsyncTask para recibir las solicitudes de amistad enviadas
 */
public class SolicitudesEnviadasTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public SolicitudesEnviadasTask(ArrayList<Object> data, MainActivity mainActivity){
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


            ArrayList<String> usuarios = new ArrayList<>();

            for(Object o : response){
                ArrayList<Object> camposAmistad = (ArrayList<Object>) o;

                usuarios.add((String) camposAmistad.get(0));

            }

            DialogSolicitudesEnviadas dialogSolicitudesEnviadas = DialogSolicitudesEnviadas.newInstance(usuarios);
            dialogSolicitudesEnviadas.show(mainActivity.getSupportFragmentManager(), "custom_dialog");


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
