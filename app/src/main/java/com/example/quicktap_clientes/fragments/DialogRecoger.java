package com.example.quicktap_clientes.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.ConnectToServerTask;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.io.IOException;
import java.util.ArrayList;

import message.Message;

/**
 * Dialog mostrado al recoger un pedido
 */
public class DialogRecoger extends DialogFragment {

    private EditText codigoEditText;

    private MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Guardar una referencia a la actividad principal
        mainActivity = (MainActivity) context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recoger, null);

        codigoEditText = dialogView.findViewById(R.id.editTextCodRecogida);

        builder.setView(dialogView)
                .setTitle("RECOGIDA")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String codigo = codigoEditText.getText().toString();

                        ArrayList<Object> data = new ArrayList<>();
                        data.add(codigo);

                        Message message = new Message("PEDIDO","RECOGIDA",data);
                        new SendMessageTask(mainActivity.conexion.getOut()).execute(message);


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogRecoger.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

