package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.util.ArrayList;

/**
 * AsyncTask para cargar las categorías del servidor
 */
public class CategoriasTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<String> categorias;

    public CategoriasTask(ArrayList<String> categorias, MainActivity mainActivity){
        this.categorias = categorias;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {

        return categorias;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {


            if(response.isEmpty() && mainActivity.homeFragment.spinnerCategorias.getAdapter() != null){
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mainActivity.homeFragment.spinnerCategorias.getAdapter();
                adapter.clear();
                adapter.notifyDataSetChanged();

            }
            else{
                mainActivity.homeFragment.setCategorias(response);
            }


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
