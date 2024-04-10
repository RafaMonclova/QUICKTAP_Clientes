package com.example.quicktap_clientes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.adapters.OrderLineAdapter;
import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.util.ArrayList;

import message.Message;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    MainActivity mainActivity;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;

    int id_pedido_actual = 0;
    String clienteAnfitrion = "";

    TextView tv_id_pedido_actual;
    TextView tv_clienteAnfitrion;
    public Button btn_crearPedido;
    public Button btn_pagar;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderFragment() {
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
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        tv_id_pedido_actual = view.findViewById(R.id.id_pedido_actual);
        tv_clienteAnfitrion = view.findViewById(R.id.cliente_anfitrion);
        btn_crearPedido = view.findViewById(R.id.btn_crearPedido);
        btn_pagar = view.findViewById(R.id.btn_pagar);

        //Si está en un pedido, el botón sirve para abandonarlo
        if(id_pedido_actual != 0){

            btn_crearPedido.setText("Dejar pedido");

        }
        else{
            btn_crearPedido.setVisibility(View.VISIBLE);
        }

        //Actualiza el total a pagar del carrito
        actualizarTotal();

        //Oculta el botón de pago para los que no sean el anfitrión del grupo
        if(mainActivity.usuario.equals(clienteAnfitrion)){
            btn_pagar.setVisibility(View.VISIBLE);
        }
        else{
            btn_pagar.setVisibility(View.GONE);
        }

        //Llama al método para ejecutar la activity de Paypal y efectuar el pago
        btn_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.processPayment();

            }
        });

        //Inicia un nuevo pedido
        btn_crearPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                String usuario = prefs.getString("usuario","Default");
                String establecimiento = prefs.getString("establecimiento","Default");

                if(btn_crearPedido.getText().toString().equals("Dejar pedido")){

                    Log.d("DEJAR","ENTRA");
                    ArrayList<Object> data = new ArrayList<>();

                    Message message = new Message("PEDIDO","DEJAR_PEDIDO",data);
                    new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

                }
                else{

                    ArrayList<Object> data = new ArrayList<>();

                    data.add(usuario);
                    data.add(establecimiento);

                    Message mensaje = new Message("PEDIDO","NUEVO_PEDIDO",data);

                    new SendMessageTask(mainActivity.conexion.getOut()).execute(mensaje);

                }

            }
        });

        //Inicializa recyclerview
        recyclerView = view.findViewById(R.id.recycler_view_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrderLineAdapter(mainActivity.carrito,mainActivity);
        recyclerView.setAdapter(mAdapter);

        //Al cargar la vista del fragment, se establecen el pedido y el cliente anfitrión en los textview
        tv_id_pedido_actual.setText("Pedido "+id_pedido_actual);
        tv_clienteAnfitrion.setText("Anfitrión: "+clienteAnfitrion);


        return view;
    }

    /**
     * Establece el listado de líneas de pedido
     * @param pedidos La lista de líneas
     */
    public void setPedidos(ArrayList<Object> pedidos){


        mainActivity.carrito = pedidos;
        mAdapter = new OrderLineAdapter(mainActivity.carrito,mainActivity);
        recyclerView.setAdapter(mAdapter);

        actualizarTotal(); //Actualiza el total nuevo


    }

    /**
     * Establece los datos de la sesión
     * @param pedido El número del pedido
     * @param cliente El cliente anfitrión
     */
    public void setDatosSesion(int pedido, String cliente){


        //Se guarda en variables de instancia el id del pedido y el cliente anfitrión
        id_pedido_actual = pedido;
        clienteAnfitrion = cliente;

        tv_id_pedido_actual.setText("Pedido "+id_pedido_actual);
        tv_clienteAnfitrion.setText("Anfitrión: "+clienteAnfitrion);

    }

    /**
     * Obtiene el total a pagar de los pedidos en estado Iniciado, que son los pendientes de pago
     * @return El total a pagar
     */
    public double getTotalPago(){

        double suma = 0;

        for(Object o : mainActivity.carrito){

            ArrayList<Object> camposLinea = (ArrayList<Object>) o;

            String estado = (String) camposLinea.get(3);

            if(estado.equals("Iniciado")){
                int cantidad = (int) camposLinea.get(4);
                double precio = (double) camposLinea.get(5);

                suma += cantidad * precio;
            }

        }

        return suma;

    }

    /**
     * Actualiza la cantidad a pagar en el botón de pagar
     */
    public void actualizarTotal(){
        //Establece en el botón en total a pagar
        Log.d("PAGAR",getTotalPago()+"");
        btn_pagar.setText("Pagar: "+getTotalPago());
    }

    /**
     * Devuelve el número del pedido actual
     * @return El número del pedido actual
     */
    public int getId_pedido_actual(){
        return id_pedido_actual;
    }

    /**
     * Devuelve el anfitrión de la sesión
     * @return El cliente anfitrión
     */
    public String getClienteAnfitrion(){
        return clienteAnfitrion;
    }


}