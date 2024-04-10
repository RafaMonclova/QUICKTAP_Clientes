package com.example.quicktap_clientes.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.SendMessageTask;
import com.example.quicktap_clientes.utils.SHA;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import message.Message;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MainActivity mainActivity;

    public TextInputLayout editTextUsuario;
    public TextInputLayout editTextCorreo;
    public TextInputLayout editTextPassw;
    public Button btn_editarPerfil;

    public ProfileFragment() {
        // Required empty public constructor
    }

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextUsuario = view.findViewById(R.id.editUsuario);
        editTextCorreo = view.findViewById(R.id.editEmail);
        editTextPassw = view.findViewById(R.id.editPassw);
        btn_editarPerfil = view.findViewById(R.id.btn_editarPerfil);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setEditTextEditable(false);
        }

        btn_editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_editarPerfil.getText().toString().equals("Guardar cambios")){

                    if(!editTextPassw.getEditText().getText().toString().isEmpty()){
                        ArrayList<Object> datosNuevos = new ArrayList<>();
                        datosNuevos.add(mainActivity.usuario);
                        datosNuevos.add(editTextUsuario.getEditText().getText().toString());
                        datosNuevos.add(editTextCorreo.getEditText().getText().toString());
                        datosNuevos.add(SHA.encrypt(editTextPassw.getEditText().getText().toString()));

                        Message message = new Message("USUARIO","ACTUALIZAR_DATOS",datosNuevos);
                        new SendMessageTask(mainActivity.conexion.getOut()).execute(message);
                    }
                    else{
                        Toast.makeText(mainActivity, "Debe rellenar la contraseña", Toast.LENGTH_SHORT).show();
                    }


                }

                boolean isEnabled = !editTextUsuario.isEnabled();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    setEditTextEditable(isEnabled);

                }

            }
        });

        ArrayList<Object> data = new ArrayList<>();
        data.add(mainActivity.usuario);

        Message message = new Message("USUARIO","GET_DATOS_USUARIO",data);
        new SendMessageTask(mainActivity.conexion.getOut()).execute(message);


        return view;
    }

    public void setDatosUsuario(ArrayList<Object> datosUsuario){

        editTextUsuario.getEditText().setText((String)datosUsuario.get(0));
        editTextCorreo.getEditText().setText((String)datosUsuario.get(1));
        //editTextPassw.getEditText().setText((String)datosUsuario.get(2));

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setEditTextEditable(boolean isEditable) {

        if (isEditable) {
            btn_editarPerfil.setText("Guardar cambios");
            editTextUsuario.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            editTextCorreo.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            editTextPassw.setVisibility(View.VISIBLE);
        } else {
            btn_editarPerfil.setText("Editar perfil");
            editTextUsuario.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
            editTextCorreo.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
            editTextPassw.setVisibility(View.GONE);
        }

        editTextUsuario.setEnabled(isEditable);
        editTextCorreo.setEnabled(isEditable);

    }
}