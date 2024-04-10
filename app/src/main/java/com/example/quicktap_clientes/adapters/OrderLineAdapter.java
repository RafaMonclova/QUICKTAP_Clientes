package com.example.quicktap_clientes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.activities.MainActivity;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import java.lang.reflect.Array;
import java.util.ArrayList;

import message.Message;

/**
 * Adapter para el recyclerview de líneas de pedido
 */
public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.OrderLineViewHolder> {

    MainActivity mainActivity;

    private ArrayList<Object> orderLineList;

    public ArrayList<Object> getListaPedidos(){

        return orderLineList;

    }

    public static class OrderLineViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView clientePedido;
        public TextView productoPedido;
        public TextView estadoPedido;
        public TextView cantidadPedido;
        public TextView precioProducto;
        public ImageView imagenProducto;
        public Button addButton;
        public Button removeButton;
        public ImageView btn_deleteOrder;

        public OrderLineViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            clientePedido = itemView.findViewById(R.id.cliente_pedido);
            productoPedido = itemView.findViewById(R.id.producto_pedido);
            estadoPedido = itemView.findViewById(R.id.estado_pedido);
            cantidadPedido = itemView.findViewById(R.id.cantidad_pedido);
            precioProducto = itemView.findViewById(R.id.precio_producto);
            imagenProducto = itemView.findViewById(R.id.image_producto);
            addButton = itemView.findViewById(R.id.button_add_cart);
            removeButton = itemView.findViewById(R.id.button_remove_cart);
            btn_deleteOrder = itemView.findViewById(R.id.btn_deleteOrder);

        }
    }

    public OrderLineAdapter(ArrayList<Object> orderLineList, MainActivity mainActivity) {
        this.orderLineList = orderLineList;
        this.mainActivity = mainActivity;
    }

    @Override
    public OrderLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        OrderLineViewHolder viewHolder = new OrderLineViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(OrderLineViewHolder holder, int position) {

        //Obtiene la LineaPedido, y asigna sus datos en los elementos de la cardview
        ArrayList<Object> lineaPedido = (ArrayList<Object>) orderLineList.get(position);

        int idLinea = (int) lineaPedido.get(0);
        holder.clientePedido.setText((String)lineaPedido.get(1)+" ha pedido:");
        holder.productoPedido.setText((String)lineaPedido.get(2));
        holder.estadoPedido.setText((String) lineaPedido.get(3));
        int cantidad = (int)lineaPedido.get(4);
        holder.cantidadPedido.setText(cantidad+"");
        double precio = (double)lineaPedido.get(5);
        holder.precioProducto.setText(precio+"");

        byte[] imageBytes = (byte[]) lineaPedido.get(6); // aquí debes reemplazar este método con la lógica que uses para obtener el array de bytes

        //Convierte el array de bytes en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        //Establece el objeto Bitmap en el ImageView
        holder.imagenProducto.setImageBitmap(bitmap);

        SharedPreferences prefs = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

        String usuarioActual = prefs.getString("usuario","Default");
        String nom= holder.clientePedido.getText().toString().substring(0,holder.clientePedido.getText().toString().indexOf(" "));


        //Si el usuario actual es el que pide, puede borrar la línea, sumar o restar cantidad
        if(!nom.equals(usuarioActual)){


            if(mainActivity.orderFragment.getClienteAnfitrion().equals(usuarioActual)){
                holder.btn_deleteOrder.setVisibility(View.VISIBLE);
                holder.removeButton.setVisibility(View.VISIBLE);
                holder.addButton.setVisibility(View.VISIBLE);
            }
            else{
                holder.btn_deleteOrder.setVisibility(View.INVISIBLE);
                holder.removeButton.setVisibility(View.INVISIBLE);
                holder.addButton.setVisibility(View.INVISIBLE);
            }


        }
        //Si no, oculta los botones
        else{
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.addButton.setVisibility(View.VISIBLE);

            holder.btn_deleteOrder.setVisibility(View.VISIBLE);
        }

        //Si la cantidad es 0, no deja restar más
        if(cantidad == 0){
            holder.removeButton.setEnabled(false);
        }
        else{
            holder.removeButton.setEnabled(true);
        }

        //Si la línea de pedido no está en estado Iniciado, no se puede modificar
        if(!holder.estadoPedido.getText().toString().equals("Iniciado")){
            holder.removeButton.setVisibility(View.INVISIBLE);
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.btn_deleteOrder.setVisibility(View.INVISIBLE);

            //Si la línea de pedido está en estado Recoger, muestra la notificación con el código de recogida
            if(holder.estadoPedido.getText().toString().equals("Recoger")){
                holder.estadoPedido.setText("Recoger \n"+lineaPedido.get(7));
                mainActivity.showNotification("Pedido listo!","Codigo: "+lineaPedido.get(7));
            }
        }

        //Aumenta la cantidad de la línea y notifica a los invitados
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentQuantity = Integer.parseInt(holder.cantidadPedido.getText().toString());
                currentQuantity++;

                lineaPedido.set(4,currentQuantity);
                orderLineList.set(holder.getAdapterPosition(),lineaPedido);

                ArrayList<Object> nuevasCantidades = new ArrayList<>();

                for(Object o : orderLineList){

                    ArrayList<Object> campos = (ArrayList<Object>) o;

                    nuevasCantidades.add(campos.get(4));

                }

                Message mensaje = new Message("PEDIDO","ACTUALIZAR_PEDIDO",nuevasCantidades);
                new SendMessageTask(mainActivity.conexion.getOut()).execute(mensaje);

            }
        });

        //Resta la cantidad de la línea y notifica a los invitados
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentQuantity = Integer.parseInt(holder.cantidadPedido.getText().toString());
                currentQuantity--;

                lineaPedido.set(4,currentQuantity);
                orderLineList.set(holder.getAdapterPosition(),lineaPedido);

                ArrayList<Object> nuevasCantidades = new ArrayList<>();

                for(Object o : orderLineList){

                    ArrayList<Object> campos = (ArrayList<Object>) o;

                    nuevasCantidades.add(campos.get(4));

                }

                //Si la cantidad es 0, lo elimina
                if(currentQuantity == 0){
                    eliminarElemento(holder.getAdapterPosition());
                }
                else{
                    Message mensaje = new Message("PEDIDO","ACTUALIZAR_PEDIDO",nuevasCantidades);
                    new SendMessageTask(mainActivity.conexion.getOut()).execute(mensaje);
                }



            }
        });

        //Elimina una línea
        holder.btn_deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eliminarElemento(holder.getAdapterPosition());

            }
        });



    }

    /**
     * Borra el elemento de la lista, y la envía al servidor. El servidor actualiza la lista de lineas de pedido por la
     * enviada por el cliente, y notifica del cambio a los invitados
     * @param posicion Posición donde se encuentra la línea en la lista
     */
    public void eliminarElemento(int posicion) {

        ArrayList<Object> data = new ArrayList<>();
        data.add(orderLineList.get(posicion));


        Message message = new Message("PEDIDO","BORRAR_LINEA_PEDIDO",data);
        new SendMessageTask(mainActivity.conexion.getOut()).execute(message);

        orderLineList.remove(posicion);
        notifyDataSetChanged();

    }

    /**
     * Contador de líneas de pedido
     * @return El contador de líneas
     */
    @Override
    public int getItemCount() {
        return orderLineList.size();
    }

    /**
     * Limpia la lista
     */
    public void clear() {

        orderLineList.clear();
        notifyDataSetChanged();
    }
}
