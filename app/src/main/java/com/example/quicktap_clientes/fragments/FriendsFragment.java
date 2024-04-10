package com.example.quicktap_clientes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.adapters.FriendsAdapter;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.util.ArrayList;

import message.Message;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    MainActivity mainActivity;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;

    ImageButton btnAñadirAmigo;
    ImageButton btnSolicitudesAmigo;
    EditText editTextAmigo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
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
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        //Inicializa el recyclerview
        recyclerView = view.findViewById(R.id.recycler_view_friends);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FriendsAdapter(mainActivity.amigos,mainActivity);
        recyclerView.setAdapter(mAdapter);

        editTextAmigo = view.findViewById(R.id.et_search);
        btnAñadirAmigo = view.findViewById(R.id.ib_add_friend);
        btnSolicitudesAmigo = view.findViewById(R.id.ib_friend_requests);

        //Envía una solicitud de amistad
        btnAñadirAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

                String amigo = editTextAmigo.getText().toString();

                if(amigo != ""){

                    if(amigo.equals(prefs.getString("usuario","Default"))){
                        Toast.makeText(mainActivity, "El usuario no puedes ser tú mismo", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        editTextAmigo.setText("");
                        ArrayList<Object> data = new ArrayList<>();
                        data.add(amigo);

                        Message message = new Message("USUARIO","AÑADIR_AMIGO",data);
                        new SendMessageTask(mainActivity.conexion.getOut()).execute(message);
                    }


                }
                else{
                    Toast.makeText(mainActivity, "Debe rellenar el campo", Toast.LENGTH_SHORT).show();
                }



            }
        });

        //Obtiene las solicitudes enviadas
        btnSolicitudesAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message message = new Message("USUARIO","GET_SOLICITUDES_ENVIADAS",new ArrayList<Object>());

                new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

            }
        });

        //Inflate the layout for this fragment
        return view;
    }

    /**
     * Establece la lista de amigos
     * @param amigos Lista de amigos
     */
    public void setAmigos(ArrayList<ArrayList<Object>> amigos){

        mainActivity.amigos = amigos;
        mAdapter = new FriendsAdapter(mainActivity.amigos,mainActivity);
        recyclerView.setAdapter(mAdapter);


    }
}