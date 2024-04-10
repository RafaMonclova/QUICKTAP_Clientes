package com.example.quicktap_clientes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.adapters.ProductAdapter;
import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.tasks.LineaPedidosTask;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import message.Message;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MainActivity mainActivity;
    public LoadingDialogFragment loadingDialogFragment;

    public Spinner spinnerCategorias;

    public TextView establecimientoSeleccionado;

    public Button floatingButton;

    public ImageView imgEstabl;

    /**************************PRODUCTOS**************************************/
    public RecyclerView recyclerView;
    public ProductAdapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Guardar una referencia a la actividad principal
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    public HomeFragment() {
        // Required empty public constructor

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loadingDialogFragment = new LoadingDialogFragment(mainActivity);
        loadingDialogFragment.show(mainActivity.getSupportFragmentManager(), "loading_dialog");

        floatingButton = view.findViewById(R.id.floatingButton);
        floatingButton.setVisibility(View.GONE);

        spinnerCategorias = view.findViewById(R.id.spinnerCategorias);
        establecimientoSeleccionado = view.findViewById(R.id.establecimientoSeleccionado);
        establecimientoSeleccionado.setText(mainActivity.establecimiento);

        //Inicializa recyclerview
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);

        //Carga las categorías disponibles
        mainActivity.cargarCategorias(establecimientoSeleccionado.getText().toString());

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String valorSeleccionado = parentView.getItemAtPosition(position).toString();

                mainActivity.cargarProductos(establecimientoSeleccionado.getText().toString(),valorSeleccionado);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                ProductAdapter adapter = (ProductAdapter) recyclerView.getAdapter();
                adapter.clear();
                adapter.notifyDataSetChanged();

            }

        });

        //Botón flotante para añadir los productos seleccionados al carrito
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Obtiene las SharedPreferences, donde se guarda el producto, el establecimiento y la cantidad de cada uno
                SharedPreferences prefs = mainActivity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences prefs2 = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

                Map<String, ?> productosAñadidos = prefs.getAll();

                ArrayList<Object> data = new ArrayList<>();

                //Al recorrerlas, se obtiene los datos de la linea de pedido a enviar al servidor.
                //Se envía además el nombre del cliente y el pedido
                for (Map.Entry<String, ?> entry : productosAñadidos.entrySet()) {
                    ArrayList<Object> lineaPedido = new ArrayList<>();

                    String productoEstabl = entry.getKey();
                    String[] valores = productoEstabl.split("\\;");

                    String producto = valores[0];
                    String establecimiento = valores[1];
                    int cantidad = (int) entry.getValue();

                    lineaPedido.add(prefs2.getInt("pedido",0));
                    lineaPedido.add(producto);
                    lineaPedido.add(establecimiento);
                    lineaPedido.add(prefs2.getString("usuario","Default"));
                    lineaPedido.add(cantidad);

                    if(cantidad > 0){
                        data.add(lineaPedido);
                    }



                }

                //Carga las líneas en el OrderFragment
                mainActivity.cargarLineaPedidos(data);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                floatingButton.setVisibility(View.GONE);
                Toast.makeText(mainActivity, "Añadidas "+data.size()+ " líneas al pedido", Toast.LENGTH_SHORT).show();

            }
        });

        //Carga la imagen de la API de GoogleMaps del sitio
        imgEstabl = view.findViewById(R.id.imgEstabl);
        if(mainActivity.urlImgEstabl != null){
            Glide.with(mainActivity).load(mainActivity.urlImgEstabl)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgEstabl);
        }


        return view;
    }


    /**
     * Recibe los productos del servidor. Se cargan al iniciar el fragment
     * @param productos Lista de productos
     */
    public void setProductos(ArrayList<ArrayList<Object>> productos){

        SharedPreferences prefs = mainActivity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        for(ArrayList<Object> producto : productos){

            String nombreProducto = (String) producto.get(0);
            String establecimiento = establecimientoSeleccionado.getText().toString();

            //Obtiene la cantidad de ese producto
            int cantidadGuardada = prefs.getInt(nombreProducto+";"+establecimiento, 0);

            producto.add(cantidadGuardada);

        }

        //Establece el adapter al recyclerview
        mAdapter = new ProductAdapter(productos,mainActivity);
        recyclerView.setAdapter(mAdapter);

    }

    /**
     * Recibe las categorías del establecimiento. Se cargan al iniciar el fragment
     * @param categorias Lista de categorías
     */
    public void setCategorias(ArrayList<String> categorias){

        //Crea un ArrayAdapter para vincular el ArrayList al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity,
                R.layout.spinner_item, categorias);

        //Configura el diseño de la lista desplegable del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Asigna el ArrayAdapter al Spinner
        spinnerCategorias.setAdapter(adapter);

    }

}