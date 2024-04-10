package com.example.quicktap_clientes.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quicktap_clientes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog que muestra las solicitudes enviadas por el cliente
 */
public class DialogSolicitudesEnviadas extends DialogFragment {
    private List<String> stringList;

    public static DialogSolicitudesEnviadas newInstance(ArrayList<String> strings) {
        DialogSolicitudesEnviadas fragment = new DialogSolicitudesEnviadas();
        Bundle args = new Bundle();
        args.putStringArrayList("strings", strings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stringList = getArguments().getStringArrayList("strings");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_solicitudes_enviadas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.listViewSolicitudes);

        //Configura el adaptador para el listado de solicitudes
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(adapter);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Solicitudes enviadas");
        return dialog;
    }
}

