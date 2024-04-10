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
 * Dialog para aceptar o rechazar una invitación a un pedido
 */
public class InviteDialog extends DialogFragment {

    String titulo;
    String mensaje;
    MainActivity mainActivity;

    public InviteDialog(String titulo, String mensaje, MainActivity mainActivity){
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {

                    ArrayList<Object> data = new ArrayList<>();
                    String remitente = titulo.substring(0, titulo.indexOf(" "));
                    data.add(remitente);
                    data.add(true);

                    Message message = new Message("PEDIDO","RESPUESTA_INVITACION",data);
                    new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

                })
                .setNegativeButton("Rechazar", (dialog, which) -> {
                    //Acciones al pulsar el botón Rechazar
                });
        return builder.create();
    }
}
