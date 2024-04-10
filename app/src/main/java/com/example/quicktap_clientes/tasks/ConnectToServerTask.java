package com.example.quicktap_clientes.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.quicktap_clientes.activities.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import message.Message;

/**
 * AsyncTask para realizar la conexión al servidor
 */
public class ConnectToServerTask extends AsyncTask<Void, Message, Void>  {


    String ip;
    int puerto;

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    Activity context;

    boolean conectado = false;

    public ConnectToServerTask(String ip, int puerto,Activity context){
        this.ip = ip;
        this.puerto = puerto;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, puerto), 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            conectado = true;

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Conectado!", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            conectado = false;
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Sin conexión", Toast.LENGTH_SHORT).show();

                }
            });



        }
        return null;
    }



    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
}
