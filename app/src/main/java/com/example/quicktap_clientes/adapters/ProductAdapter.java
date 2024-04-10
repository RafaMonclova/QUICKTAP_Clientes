package com.example.quicktap_clientes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.Map;

/**
 * Adapter para el recyclerview de productos
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    MainActivity mainActivity;

    public ArrayList<ArrayList<Object>> productList;

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public ImageView productImage;
        public Button addButton;
        public Button subtractButton;
        public TextView quantityTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.image_view_product);
            addButton = itemView.findViewById(R.id.button_add);
            subtractButton = itemView.findViewById(R.id.button_remove);
            quantityTextView = itemView.findViewById(R.id.productCount);
        }
    }

    public ProductAdapter(ArrayList<ArrayList<Object>> productList,MainActivity mainActivity) {
        this.productList = productList;
        this.mainActivity = mainActivity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ProductViewHolder viewHolder = new ProductViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        //Obtiene el producto, y asigna sus datos en los elementos de la cardview
        ArrayList<Object> product = productList.get(position);

        SharedPreferences prefsU = mainActivity.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorU = prefsU.edit();

        holder.productName.setText((String)product.get(0));
        holder.productDescription.setText((String)product.get(1));
        double precio = (Double)product.get(2);
        holder.productPrice.setText(precio+"");

        byte[] imageBytes = (byte[]) product.get(4); // aquí debes reemplazar este método con la lógica que uses para obtener el array de bytes

        //Convierte el array de bytes en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        //Establece el objeto Bitmap en el ImageView
        holder.productImage.setImageBitmap(bitmap);

        int cantidad = (int) product.get(5);

        holder.quantityTextView.setText(cantidad+"");

        //Si la cantidad es mayor a 0, activa el botón de restar productos
        if(cantidad > 0){
            holder.subtractButton.setEnabled(true);
        }

        //Si no hay pedido, desactiva todos los botones
        if(mainActivity.orderFragment.getId_pedido_actual() == 0){
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.subtractButton.setVisibility(View.INVISIBLE);
            holder.quantityTextView.setVisibility(View.INVISIBLE);

            //Si no hay stock, muestra el mensaje Agotado
            int stock = (int) product.get(3);
            if(stock == 0){
                holder.quantityTextView.setVisibility(View.VISIBLE);
                holder.quantityTextView.setText("Agotado");
            }

        }
        else{
            holder.addButton.setVisibility(View.VISIBLE);
            holder.subtractButton.setVisibility(View.VISIBLE);
            holder.quantityTextView.setVisibility(View.VISIBLE);

            int stock = (int) product.get(3);
            if(stock == 0){
                holder.addButton.setVisibility(View.INVISIBLE);
                holder.subtractButton.setVisibility(View.INVISIBLE);
                holder.quantityTextView.setVisibility(View.VISIBLE);
                holder.quantityTextView.setText("Agotado");
            }
        }

        //Añade productos al carrito
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
                currentQuantity++;
                holder.quantityTextView.setText(String.valueOf(currentQuantity));
                holder.subtractButton.setEnabled(true);

                //Los productos del carrito se guardan en la SharedPreferences llamada MisPreferencias
                //Accede a ella para obtener los datos de cada producto añadido
                SharedPreferences prefs = mainActivity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putInt(holder.productName.getText().toString()+";"+mainActivity.homeFragment.establecimientoSeleccionado.getText().toString(),currentQuantity);
                editor.apply();

                if(carritoVacio()){
                    //Oculta botón de añadir al carrito
                    mainActivity.homeFragment.floatingButton.setVisibility(View.GONE);
                }
                else{
                    //Muestra el botón de añadir al carrito
                    mainActivity.homeFragment.floatingButton.setVisibility(View.VISIBLE);
                }

            }
        });


        //Quita productos del carrito
        holder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
                if (currentQuantity > 0) {
                    currentQuantity--;
                    holder.quantityTextView.setText(String.valueOf(currentQuantity));
                    if(holder.quantityTextView.getText().toString().equals("0")){
                        holder.subtractButton.setEnabled(false);
                    }

                    SharedPreferences prefs = mainActivity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(holder.productName.getText().toString()+";"+mainActivity.homeFragment.establecimientoSeleccionado.getText().toString(),currentQuantity);
                    editor.apply();

                    if(carritoVacio()){
                        //Oculta botón de añadir al carrito
                        mainActivity.homeFragment.floatingButton.setVisibility(View.GONE);
                    }
                    else{
                        //Muestra el botón de añadir al carrito
                        mainActivity.homeFragment.floatingButton.setVisibility(View.VISIBLE);
                    }

                }

            }
        });



    }

    /**
     * Comprueba si el carrito está vacío
     * @return Devuelve un boolean que indica si el carrito está vacío
     */
    public boolean carritoVacio(){

        boolean carritoVacio = true;

        SharedPreferences prefs = mainActivity.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        Map<String, ?> todasLasPreferencias = prefs.getAll();

        for (Map.Entry<String, ?> entry : todasLasPreferencias.entrySet()) {
            String clave = entry.getKey();
            Object valor = entry.getValue();

            if (valor != null && !valor.toString().equals("0")) {
                carritoVacio = false;
                break;
            }
        }

        return carritoVacio;

    }

    /**
     * Contador de productos
     * @return El contador de productos
     */
    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Limpia la lista
     */
    public void clear() {

        productList.clear();
        notifyDataSetChanged();
    }
}
