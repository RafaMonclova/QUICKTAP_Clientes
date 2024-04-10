package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MapActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para obtener los establecimientos del servidor
 */
public class EstablecimientosTask extends AsyncTask<Void, Void, ArrayList> {

    MapActivity mapActivity;
    ObjectInputStream in;


    public EstablecimientosTask(ObjectInputStream in, MapActivity mapActivity){
        this.in = in;
        this.mapActivity = mapActivity;

    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {
        try {
            Message response = (Message) in.readObject();

            return response.getData();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {

            mapActivity.añadirMarcadores(response);


        } else {
            // Error de conexión
            Toast.makeText(mapActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
