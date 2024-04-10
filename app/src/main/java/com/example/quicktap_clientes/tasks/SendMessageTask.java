package com.example.quicktap_clientes.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;

import message.Message;

/**
 * AsyncTask para enviar mensajes al servidor
 */
public class SendMessageTask extends AsyncTask<Message, Void, Void> {

    ObjectOutputStream out;

    public SendMessageTask(ObjectOutputStream out){
        this.out = out;
    }

    @Override
    protected Void doInBackground(Message... messages) {
        try {
            Message message = messages[0];
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
