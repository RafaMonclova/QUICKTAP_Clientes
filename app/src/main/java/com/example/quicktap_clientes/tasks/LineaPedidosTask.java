package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para obtener las líneas de pedido de la sesión
 */
public class LineaPedidosTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> lineas;

    public LineaPedidosTask(ArrayList<Object> lineas, MainActivity mainActivity){
        this.lineas = lineas;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {

        return lineas;
    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {

            mainActivity.orderFragment.setPedidos(response);


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
