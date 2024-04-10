package com.example.quicktap_clientes.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.utils.SHA;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextInputLayout userEditText;
    private TextInputLayout emailEditText;
    private TextInputLayout passwordEditText;
    private TextInputLayout confirmPasswordEditText;
    private Button btn_registrar;

    private LoginActivity loginActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Guardar una referencia a la actividad principal
        loginActivity = (LoginActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginActivity = null;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrarFragment() {
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
    public static RegistrarFragment newInstance(String param1, String param2) {
        RegistrarFragment fragment = new RegistrarFragment();
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
        View view = inflater.inflate(R.layout.fragment_registrar, container, false);

        // Obtener referencias a los elementos de la interfaz de usuario
        userEditText = view.findViewById(R.id.registrarUsuario);
        emailEditText = view.findViewById(R.id.registrarEmail);
        passwordEditText = view.findViewById(R.id.registrarPassword);
        confirmPasswordEditText = view.findViewById(R.id.registrarConfirmPassword);
        btn_registrar = view.findViewById(R.id.btn_registrar);

        // Agregar un Listener al botón de registro
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los campos ingresados por el usuario
                String usuario = userEditText.getEditText().getText().toString();
                String email = emailEditText.getEditText().getText().toString();
                String password = passwordEditText.getEditText().getText().toString();
                String confirmPassword = confirmPasswordEditText.getEditText().getText().toString();

                if(!password.equals(confirmPassword)){
                    Toast.makeText(loginActivity, "Debe confirmar la contraseña", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        loginActivity.registrar(usuario,email, SHA.encrypt(password));
                    }catch(Exception ex){
                        Toast.makeText(loginActivity, "Sin conexión", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });


        // Inflate the layout for this fragment
        return view;
    }
}