package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.adapters.ProductAdapter;

import java.util.ArrayList;

/**
 * AsyncTask para obtener los productos de un establecimiento
 */
public class ProductosTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<ArrayList<Object>> productos;

    public ProductosTask(ArrayList<ArrayList<Object>> productos, MainActivity mainActivity){
        this.productos = productos;
        this.mainActivity = mainActivity;

    }


    @Override
    protected ArrayList<ArrayList<Object>> doInBackground(Void... voids) {

        return productos;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {


            if(response.isEmpty() && mainActivity.homeFragment.recyclerView.getAdapter() != null){
                ProductAdapter adapter = (ProductAdapter) mainActivity.homeFragment.recyclerView.getAdapter();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
            else{

                mainActivity.homeFragment.setProductos(response);

            }

            mainActivity.homeFragment.loadingDialogFragment.dismiss();



        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
