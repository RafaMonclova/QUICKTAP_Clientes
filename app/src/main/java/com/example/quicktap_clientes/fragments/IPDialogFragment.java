package com.example.quicktap_clientes.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.ConnectToServerTask;

import java.io.IOException;

/**
 * Dialog para cambiar datos de red
 */
public class IPDialogFragment extends DialogFragment {

    private EditText ipEditText;
    private EditText portEditText;

    private LoginActivity loginActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Guardar una referencia a la actividad principal
        loginActivity = (LoginActivity) context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ip, null);

        ipEditText = dialogView.findViewById(R.id.editTextIP);
        portEditText = dialogView.findViewById(R.id.editTextPort);

        builder.setView(dialogView)
                .setTitle("Configuración de IP y Puerto")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String ip = ipEditText.getText().toString();
                        String puerto = portEditText.getText().toString();

                        try {
                            loginActivity.conexion.getSocket().close(); //Cierra el socket y vuelve a iniciar la conexión con los nuevos datos
                        } catch (IOException e) {

                        }catch (NullPointerException e){

                        }

                        loginActivity.conexion = new ConnectToServerTask(ip,Integer.parseInt(puerto),loginActivity);
                        loginActivity.conexion.execute();

                        SharedPreferences prefs = loginActivity.getSharedPreferences("DatosConexion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ip",ip);
                        editor.putString("puerto",puerto);
                        editor.apply();


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IPDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

