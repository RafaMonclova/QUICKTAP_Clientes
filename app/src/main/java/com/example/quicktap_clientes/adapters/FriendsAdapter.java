package com.example.quicktap_clientes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import message.Message;

/**
 * Adaptador para el recyclerview de amigos
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    MainActivity mainActivity;

    public ArrayList<ArrayList<Object>> friendsList;

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView friendName;
        public TextView friendStatus;
        public TextView friendConnected;
        public ImageButton btnInvitar;
        public ImageButton btnAceptar;
        public ImageButton btnRechazar;



        public FriendViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            friendName = itemView.findViewById(R.id.friend_name);
            friendStatus = itemView.findViewById(R.id.friend_status);
            friendConnected = itemView.findViewById(R.id.friend_connected);
            btnInvitar = itemView.findViewById(R.id.image_view_invitar);
            btnAceptar = itemView.findViewById(R.id.image_view_aceptar);
            btnRechazar = itemView.findViewById(R.id.image_view_rechazar);


        }
    }

    public FriendsAdapter(ArrayList<ArrayList<Object>> friendsList, MainActivity mainActivity) {
        this.friendsList = friendsList;
        this.mainActivity = mainActivity;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {

        //Obtiene el amigo, y asigna sus datos en los elementos de la cardview
        ArrayList<Object> friend = friendsList.get(position);

        //Acepta una solicitud de amistad
        holder.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

                ArrayList<Object> data = new ArrayList<>();

                data.add(holder.friendName.getText().toString()); //Remitente de la solicitud
                data.add(prefs.getString("usuario","Default")); //Receptor (usuario actual)
                data.add(true);

                Message message = new Message("USUARIO","SOLICITUD_AMISTAD",data);
                new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

            }
        });

        //Rechaza una solicitud de amistad
        holder.btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

                ArrayList<Object> data = new ArrayList<>();

                data.add(holder.friendName.getText().toString()); //Remitente de la solicitud
                data.add(prefs.getString("usuario","Default")); //Receptor (usuario actual)
                data.add(false);

                Message message = new Message("USUARIO","SOLICITUD_AMISTAD",data);
                new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

            }
        });

        //Invita a un amigo al pedido actual
        holder.btnInvitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);


                ArrayList<Object> data = new ArrayList<>();
                data.add(holder.friendName.getText().toString());
                data.add(prefs.getString("establecimiento","Default"));

                Message message = new Message("PEDIDO","INVITAR",data);
                new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

            }
        });

        //Datos de los amigos
        holder.friendName.setText((String)friend.get(0));
        holder.friendStatus.setText((String)friend.get(1));

        try{
            holder.friendConnected.setText((String) friend.get(2));
        }catch (IndexOutOfBoundsException ex){
            holder.friendConnected.setText("Desconectado"); //Si no encuentra el campo conectado, lo pone como desconectado
        }


        //Si es una solicitud pendiente
        if(holder.friendStatus.getText().toString().equals("Pendiente")){
            holder.btnInvitar.setVisibility(View.GONE);
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.friendConnected.setVisibility(View.GONE);
        }
        else{

            holder.btnAceptar.setVisibility(View.GONE);
            holder.btnRechazar.setVisibility(View.GONE);
            holder.friendStatus.setVisibility(View.GONE);
            holder.friendConnected.setVisibility(View.VISIBLE);

            //Si el amigo está conectado
            if(holder.friendConnected.getText().toString().equals("Conectado")){
                holder.friendConnected.setTextColor(Color.parseColor("#33cc33"));

                //Si está en algún pedido, puede invitar a sus amigos a él
                if(mainActivity.orderFragment.getId_pedido_actual() != 0){

                    holder.btnInvitar.setVisibility(View.VISIBLE);

                }
                else{

                    holder.btnInvitar.setVisibility(View.GONE);

                }

            }
            else{
                holder.friendConnected.setTextColor(Color.parseColor("#c2d6d6"));
                holder.btnInvitar.setVisibility(View.GONE);
            }

        }

    }

    /**
     * Contador de amigos
     * @return El contador de amigos
     */
    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    /**
     * Limpia la lista de amigos
     */
    public void clear() {

        friendsList.clear();
        notifyDataSetChanged();
    }
}
