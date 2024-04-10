package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.fragments.LoginCallback;
import com.example.quicktap_clientes.fragments.RegisterCallback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para registrar un nuevo usuario en el servidor y recibir una respuesta
 */
public class RegistrarTask extends AsyncTask<Void, Void, Message> {

    LoginActivity loginActivity;
    ObjectInputStream in;

    RegisterCallback callback;

    public RegistrarTask(ObjectInputStream in, LoginActivity loginActivity){
        this.in = in;
        this.loginActivity = loginActivity;
        this.callback = loginActivity;
    }

    @Override
    protected Message doInBackground(Void... voids) {
        try {
            Message response = (Message) in.readObject();

            return response;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Message response) {
        if (response != null) {

            boolean exito = (boolean) response.getData().get(0);

            if(exito){
                String usuario = (String) response.getData().get(1);

                callback.onRegisterSuccess("Usuario registrado: ",usuario);

            }
            else{ //Datos incorrectos
                callback.onRegisterFailure("Ya existe un usuario con el nombre o email introducidos");
            }


        } else {
            // Error de conexión
            Toast.makeText(loginActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
