package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.LoginActivity;
import com.example.quicktap_clientes.fragments.LoginCallback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para realizar el login
 */
public class LoginTask extends AsyncTask<Void, Void, Message> {

    LoginActivity loginActivity;
    ObjectInputStream in;

    LoginCallback callback;

    public LoginTask(ObjectInputStream in, LoginActivity loginActivity){
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
                ArrayList<String> roles = (ArrayList<String>) response.getData().get(2);

                callback.onLoginSuccess("Bienvenido",usuario);


            }
            else{ //Datos incorrectos
                callback.onLoginFailure("Credenciales inválidas");
            }



        } else {
            // Error de conexión
            Toast.makeText(loginActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
    }


}
