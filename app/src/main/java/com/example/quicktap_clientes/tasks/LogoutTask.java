package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para cerrar sesión en el servidor
 */
public class LogoutTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public LogoutTask(ArrayList<Object> data, MainActivity mainActivity){
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

                mainActivity.logout();

            }


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
