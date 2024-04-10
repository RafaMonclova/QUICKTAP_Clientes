package com.example.quicktap_clientes.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.util.ArrayList;

import message.Message;

/**
 * Dialog para confirmar el cierre de sesión
 */
public class LogoutDialog extends DialogFragment {

    String usuario;
    MainActivity mainActivity;

    public LogoutDialog(String usuario, MainActivity mainActivity){
        this.usuario = usuario;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Desconexión")
                .setMessage("Cerrar sesión?")
                .setPositiveButton("Aceptar", (dialog, which) -> {

                    ArrayList<Object> data = new ArrayList<>();
                    data.add(usuario);

                    Message message = new Message("SERVIDOR","LOGOUT",data);
                    new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

                })
                .setNegativeButton("Rechazar", (dialog, which) -> {
                    //Acciones al pulsar el botón Rechazar
                });
        return builder.create();
    }
}
