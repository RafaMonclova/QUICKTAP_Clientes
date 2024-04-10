package com.example.quicktap_clientes.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.MainActivity;

/**
 * Dialog con gif de carga
 */
public class LoadingDialogFragment extends DialogFragment {

    MainActivity mainActivity;

    public LoadingDialogFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public static AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_loading_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //Evita que se cierre al hacer clic fuera de la ventana



        return dialog;

    }
}
