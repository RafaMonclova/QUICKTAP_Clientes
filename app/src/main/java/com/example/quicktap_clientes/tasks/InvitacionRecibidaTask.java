package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.fragments.InviteDialog;

import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para obtener invitaciones a pedidos
 */
public class InvitacionRecibidaTask extends AsyncTask<Void, Void, ArrayList> {

    MainActivity mainActivity;
    ArrayList<Object> data;


    public InvitacionRecibidaTask(ArrayList<Object> data, MainActivity mainActivity){
        this.data = data;
        this.mainActivity = mainActivity;

    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {

        return data;

    }

    @Override
    protected void onPostExecute(ArrayList response) {
        if (response != null) {

            String remitente = (String) response.get(0);
            String establecimeinto = (String) response.get(1);

            InviteDialog inviteDialog = new InviteDialog(remitente+" te ha invitado en:",establecimeinto,mainActivity);
            mainActivity.showNotification("Invitación a pedido recibida","De: "+remitente,inviteDialog);


        } else {
            // Error de conexión
            Toast.makeText(mainActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
