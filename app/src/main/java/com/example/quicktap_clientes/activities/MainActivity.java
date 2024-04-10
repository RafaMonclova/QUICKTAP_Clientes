package com.example.quicktap_clientes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.adapters.OrderLineAdapter;
import com.example.quicktap_clientes.adapters.ProductAdapter;
import com.example.quicktap_clientes.fragments.HomeFragment;
import com.example.quicktap_clientes.fragments.InviteDialog;
import com.example.quicktap_clientes.fragments.LogoutDialog;
import com.example.quicktap_clientes.fragments.OrderFragment;
import com.example.quicktap_clientes.fragments.ProfileFragment;
import com.example.quicktap_clientes.fragments.FriendsFragment;
import com.example.quicktap_clientes.tasks.ActualizarDatosUsuarioTask;
import com.example.quicktap_clientes.tasks.AmigosTask;
import com.example.quicktap_clientes.tasks.CategoriasTask;
import com.example.quicktap_clientes.tasks.ConnectToServerTask;
import com.example.quicktap_clientes.tasks.DatosUsuarioTask;
import com.example.quicktap_clientes.tasks.EstadoSolicitudTask;
import com.example.quicktap_clientes.tasks.InvitacionRecibidaTask;
import com.example.quicktap_clientes.tasks.LineaPedidosTask;
import com.example.quicktap_clientes.tasks.LogoutTask;
import com.example.quicktap_clientes.tasks.ProductosTask;
import com.example.quicktap_clientes.tasks.SendMessageTask;

import com.example.quicktap_clientes.tasks.SolicitudAmigoTask;
import com.example.quicktap_clientes.tasks.SolicitudesEnviadasTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import message.Message;


public class MainActivity extends AppCompatActivity  {

    //Clase para la conexión
    public static ConnectToServerTask conexion;

    //Constantes
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;
    public static final int REQUEST_CODE_PAYPAL_PAYMENT = 7171;
    public static final String CLIENT_ID = "AQy7K5Ja1NpT2kNqj4kcyIcQ_gWms2yrpjxbHTmM94PyrjYL4Mk1RnI7G4EyJKDammPnUVf8IxZkY2ps";
    public PayPalConfiguration paypalConfig;

    //Menú
    public BottomNavigationView bottomNavigationView;

    //Fragments
    public HomeFragment homeFragment = new HomeFragment();
    public OrderFragment orderFragment = new OrderFragment();
    public ProfileFragment profileFragment = new ProfileFragment();
    public FriendsFragment friendsFragment = new FriendsFragment();

    //Datos del usuario y establecimiento
    public String establecimiento;
    public String usuario;
    public String urlImgEstabl;


    //public static Socket socket;
    //public static ObjectOutputStream out;
    //public static ObjectInputStream in;

    //Colecciones
    public static ArrayList<Object> carrito = new ArrayList<>();
    public static ArrayList<ArrayList<Object>> amigos = new ArrayList<>();

    //Hilo de escucha
    public NotificationListener notificationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();


        //Configuración de PayPal
        paypalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId(CLIENT_ID);

        //Servicio de PayPal
        Intent paypalServiceIntent = new Intent(this, PayPalService.class);
        paypalServiceIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(paypalServiceIntent);


        Intent intent = getIntent();

        //Datos enviados del MapActivity
        usuario = (String) intent.getExtras().get("usuario");
        establecimiento = (String) intent.getExtras().get("establecimiento");

        //Guarda en las preferencias de la app, el nombre del usuario y el establecimiento
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("usuario",usuario);
        editor.putString("establecimiento",establecimiento);
        editor.apply();

        //Obtiene la conexión al servidor, enviada desde LoginActivity
        conexion = LoginActivity.conexion;
        conexion.setContext(this);

        //Inicia el hilo de escucha
        notificationListener = new NotificationListener();
        notificationListener.startListening();

        //Setea el establecimiento para el hilo del cliente en el servidor
        ArrayList<Object> data = new ArrayList<>();
        data.add(establecimiento);
        Message message = new Message("ESTABLECIMIENTO","SET_ESTABLECIMIENTO",data);
        new SendMessageTask(conexion.getOut()).execute(message);

        //Carga los fragments en el inicio
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, friendsFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();


        //Configuración de la barra de navegación
        bottomNavigationView = findViewById(R.id.BottomNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.inicio:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, homeFragment)
                                        .commit();

                                return true;
                            case R.id.carrito:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, orderFragment)
                                        .commit();

                                return true;
                            case R.id.perfil:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, profileFragment)
                                        .commit();
                                return true;
                            case R.id.amigos:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, friendsFragment)
                                        .commit();


                                //Envía una petición para recibir los amigos al entrar al fragment
                                Message message = new Message("USUARIO","GET_AMIGOS",new ArrayList<Object>());
                                new SendMessageTask(conexion.getOut()).execute(message);

                                return true;
                        }
                        return false;
                    }
                });




    }


    /**
     * Carga los productos al recyclerview
     * @param establecimiento Establecimiento donde buscar los productos
     * @param categoria Categoría donde buscar los productos
     */
    public void cargarProductos(String establecimiento,String categoria){

        //Enviar el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
        ArrayList<Object> data = new ArrayList<>();
        data.add(establecimiento);
        data.add(categoria);

        Message mensaje = new Message("PRODUCTO","GET_PRODUCTOS_CATEGORIA",data);

        new SendMessageTask(conexion.getOut()).execute(mensaje);

    }

    /**
     * Envía al servidor los datos de las líneas de pedido
     * @param lineasPedido Las líneas de pedido a añadir
     */
    public void cargarLineaPedidos(ArrayList<Object> lineasPedido){

        //Enviar el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
        Message mensaje = new Message("PEDIDO","ENVIAR_LINEASPEDIDOS",lineasPedido);

        new SendMessageTask(conexion.getOut()).execute(mensaje);


    }

    /**
     * Carga las categorías al spinner
     * @param establecimiento El establecimiento donde buscar
     */
    public void cargarCategorias(String establecimiento){

        ArrayList<Object> data = new ArrayList<>();
        data.add(establecimiento);

        //Enviar el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
        Message mensaje = new Message("CATEGORIA","GET_CATEGORIAS",data);

        new SendMessageTask(conexion.getOut()).execute(mensaje);

    }

    /**************************************************************************/
    /*** SHARED PREFERENCES***/
    /**************************************************************************/
    //Limpia las SharedPreferences al cerrar la app
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = this.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        SharedPreferences prefs2 = this.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();
        editor2.clear();
        editor2.apply();

        //stopService(new Intent(this, PayPalService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    /**
     * Clase para el hilo de escucha. Escucha mensajes del servidor y actualiza la interfaz dependiendo del mensaje recibido
     */
    public class NotificationListener implements Runnable {

        private ObjectInputStream input;
        private boolean listening;

        public void startListening() {

            new Thread(this).start();
            listening = true;
        }
        public void stopListening(){
            listening = false;
        }

        @Override
        public void run() {
            try {

                //Obtiene flujo de entrada de la clase conexión
                input = conexion.getIn();

                //Escucha mensajes del servidor
                while (listening) {
                    Message respuesta = (Message) input.readObject();

                    switch(respuesta.getRequestType()){

                        case "LOGIN":

                            switch(respuesta.getRequestAction()){

                                case "LOGOUT":

                                    LogoutTask logoutTask = new LogoutTask(respuesta.getData(),MainActivity.this);
                                    logoutTask.execute();

                                    break;

                            }

                            break;


                        case "USUARIO":

                            switch(respuesta.getRequestAction()){

                                case "GET_AMIGOS":

                                    ArrayList<ArrayList<Object>> listaAmigos = new ArrayList<>();
                                    for(Object o : respuesta.getData()){
                                        listaAmigos.add((ArrayList<Object>) o);
                                    }

                                    AmigosTask amigosTask = new AmigosTask(listaAmigos,MainActivity.this);
                                    amigosTask.execute();

                                    break;

                                case "AÑADIR_AMIGO":


                                    SolicitudAmigoTask solicitudAmigoTask = new SolicitudAmigoTask(respuesta.getData(),MainActivity.this);
                                    solicitudAmigoTask.execute();

                                    break;


                                case "SOLICITUD_AMISTAD":


                                    EstadoSolicitudTask estadoSolicitudTask = new EstadoSolicitudTask(respuesta.getData(),MainActivity.this);
                                    estadoSolicitudTask.execute();

                                    break;

                                case "GET_SOLICITUDES_ENVIADAS":

                                    SolicitudesEnviadasTask solicitudesEnviadasTask = new SolicitudesEnviadasTask(respuesta.getData(),MainActivity.this);
                                    solicitudesEnviadasTask.execute();

                                    break;

                                case "GET_DATOS_USUARIO":

                                    DatosUsuarioTask datosUsuarioTask = new DatosUsuarioTask(respuesta.getData(),MainActivity.this);
                                    datosUsuarioTask.execute();

                                    break;

                                case "ACTUALIZAR_DATOS":

                                    ActualizarDatosUsuarioTask actualizarDatosUsuarioTask = new ActualizarDatosUsuarioTask(respuesta.getData(),MainActivity.this);
                                    actualizarDatosUsuarioTask.execute();

                                    break;


                            }

                            break;

                        case "ESTABLECIMIENTO":

                            switch(respuesta.getRequestAction()){

                                case "SET_ESTABLECIMIENTO":

                                    double latitud = (double) respuesta.getData().get(0);
                                    double longitud = (double) respuesta.getData().get(1);

                                    if(urlImgEstabl == null){
                                        urlImgEstabl = "https://maps.google.com/maps/api/staticmap?center=" + latitud + "," + longitud + "&zoom=15&size=200x200&sensor=false&key=AIzaSyAaP6rsOVudwTFDitm7dKUbSJpTBabVyFU";
                                    }


                                    Log.d("LAT",latitud+"");
                                    Log.d("LON",longitud+"");
                                    Log.d("URL",urlImgEstabl+"");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(MainActivity.this).load(urlImgEstabl)
                                                    .error(R.drawable.ic_launcher_background)
                                                    .into(homeFragment.imgEstabl);
                                        }
                                    });

                                    break;

                            }

                            break;

                        case "CATEGORIA":

                            switch(respuesta.getRequestAction()){

                                case "GET_CATEGORIAS":
                                    ArrayList<String> listaCategorias = new ArrayList<>();
                                    for(Object o : respuesta.getData()){
                                        listaCategorias.add((String) o);
                                    }

                                    if(respuesta.getData().isEmpty()){
                                        homeFragment.loadingDialogFragment.dismiss();
                                    }

                                    if(listaCategorias.isEmpty() && homeFragment.spinnerCategorias.getAdapter() != null){
                                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) homeFragment.spinnerCategorias.getAdapter();
                                        adapter.clear();
                                        adapter.notifyDataSetChanged();

                                    }
                                    else{
                                        CategoriasTask categoriasTask = new CategoriasTask(listaCategorias,MainActivity.this);
                                        categoriasTask.execute();

                                    }

                                    break;

                            }

                            break;

                        case "PRODUCTO":

                            switch(respuesta.getRequestAction()){

                                case "GET_PRODUCTOS_CATEGORIA":

                                    ArrayList<ArrayList<Object>> listaProductos = new ArrayList<>();
                                    for(Object o : respuesta.getData()){
                                        listaProductos.add((ArrayList<Object>) o);
                                    }

                                    if(listaProductos.isEmpty() && homeFragment.recyclerView.getAdapter() != null){

                                        ProductAdapter adapter = (ProductAdapter) homeFragment.recyclerView.getAdapter();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.clear();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                    }
                                    else{

                                        ProductosTask productosTask = new ProductosTask(listaProductos,MainActivity.this);
                                        productosTask.execute();

                                    }

                                    break;

                            }

                            break;

                        case "PEDIDO":

                            switch(respuesta.getRequestAction()){

                                case "NUEVO_PEDIDO":

                                    //Envía el id del pedido creado al OrderFragment
                                    int idPedidoActual = (int) respuesta.getData().get(0);
                                    String clienteAnfitrion = (String) respuesta.getData().get(1);

                                    SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("pedido",idPedidoActual);
                                    editor.apply();


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            orderFragment.setDatosSesion(idPedidoActual,clienteAnfitrion);
                                            Toast.makeText(MainActivity.this, "Pedido creado!", Toast.LENGTH_SHORT).show();
                                            orderFragment.btn_crearPedido.setText("Dejar pedido");
                                        }
                                    });

                                    break;

                                case "UNIRSE_PEDIDO":

                                    //Envía el id del pedido creado al OrderFragment
                                    int idPedidoActualU = (int) respuesta.getData().get(0);

                                    //Unido con éxito
                                    if(idPedidoActualU != 0){
                                        String clienteAnfitrionU = (String) respuesta.getData().get(1);

                                        SharedPreferences prefs2 = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor2 = prefs2.edit();
                                        editor2.putInt("pedido",idPedidoActualU);
                                        editor2.apply();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                orderFragment.setDatosSesion(idPedidoActualU,clienteAnfitrionU);
                                                orderFragment.btn_crearPedido.setText("Dejar pedido");
                                                orderFragment.setPedidos((ArrayList<Object>) respuesta.getData().get(2));

                                            }
                                        });


                                    }
                                    //No se encontró la sesión
                                    else{

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "No se encontró la sesión", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    break;

                                case "INVITAR":

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            int exito = (int) respuesta.getData().get(0);

                                            if(exito == 0){
                                                Toast.makeText(MainActivity.this, "Se ha creado la invitación", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(exito == 1){
                                                Toast.makeText(MainActivity.this, "El cliente ya pertenece a la sesión", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(exito == 2){
                                                Toast.makeText(MainActivity.this, "El cliente no pertenece al mismo establecimiento", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    break;

                                case "RESPUESTA_INVITACION":

                                    InvitacionRecibidaTask invitacionRecibidaTask = new InvitacionRecibidaTask(respuesta.getData(),MainActivity.this);
                                    invitacionRecibidaTask.execute();

                                    break;

                                case "NOTIFICAR_PEDIDOS":

                                    LineaPedidosTask lineaPedidosTask = new LineaPedidosTask(respuesta.getData(),MainActivity.this);
                                    lineaPedidosTask.execute();

                                    try {
                                        ArrayList<Object> camposLinea = (ArrayList<Object>) respuesta.getData().get(respuesta.getData().size()-1);
                                        String usuarioPide = (String) camposLinea.get(1);

                                        if(usuarioPide.equals(usuario)){

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    View view = bottomNavigationView.findViewById(R.id.carrito);
                                                    view.performClick();

                                                }
                                            });

                                        }
                                    }catch(ArrayIndexOutOfBoundsException ex){

                                    }

                                    break;


                                case "SALIR_SESION":

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            orderFragment.setDatosSesion(0,"");
                                            Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                                            orderFragment.btn_crearPedido.setText("Crear pedido");
                                            orderFragment.btn_pagar.setVisibility(View.GONE);

                                            actualizarFragment();

                                            OrderLineAdapter adapter = (OrderLineAdapter) orderFragment.recyclerView.getAdapter();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.clear();
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });



                                    break;

                            }

                            break;

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Carga el fragment de pedidos, usando la opción de menú correspondiente
     */
    public void actualizarFragment(){

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .commit();

        View view = bottomNavigationView.findViewById(R.id.carrito);
        view.performClick();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();

        view = bottomNavigationView.findViewById(R.id.inicio);
        view.performClick();

    }

    /**
     * Crea un canal de notificaciones para las notificaciones entrantes
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Muestra una notificación de invitación en la barra de notificaciones
     * @param titulo El título de la notificación
     * @param texto El texto de la notificación
     * @param inviteDialog El dialog de invitación
     */
    public void showNotification(String titulo, String texto, InviteDialog inviteDialog) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        inviteDialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Muestra una notificación en la barra de notificaciones
     * @param titulo El título de la notificación
     * @param texto El texto de la notificación
     */
    public void showNotification(String titulo, String texto) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    /**
     * Al pulsar el botón atrás, pregunta si quiere cerrar sesión en un dialog emergente
     */
    @Override
    public void onBackPressed() {

        LogoutDialog logoutDialog = new LogoutDialog(usuario,this);
        logoutDialog.show(getSupportFragmentManager(), "dialog_logout");

    }

    /**
     * Reescribe el método para verificar el estado del pago de PayPal
     * @param requestCode Código interno de PayPal
     * @param resultCode Código de estado de la operación
     * @param data Intent de la activity de PayPal
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYPAL_PAYMENT) {
            if (resultCode == RESULT_OK) {
                //El pago se completó correctamente
                PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (paymentConfirmation != null) {

                    //Muestra mensaje de éxito y envía la petición al servidor con las líneas a pagar
                    Toast.makeText(MainActivity.this, "Pago realizado!", Toast.LENGTH_SHORT).show();
                    ArrayList<Object> pedidosAPagar = new ArrayList<>();

                    Message message = new Message("PEDIDO","PAGAR",pedidosAPagar);
                    new SendMessageTask(conexion.getOut()).execute(message);
                }
            } else if (resultCode == RESULT_CANCELED) {
                //El usuario canceló el pago
                Toast.makeText(MainActivity.this, "Pago cancelado", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(MainActivity.this, "Error en el pago", Toast.LENGTH_SHORT).show();
                //Los datos adicionales enviados a PayPal son inválidos
            }
        }
    }

    /**
     * Abre la pasarela de pago con el total de pago del OrderFragment
     */
    public void processPayment() {

        PayPalPayment payment = new PayPalPayment(new BigDecimal(orderFragment.getTotalPago()), "EUR", "Descripción del pago", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL_PAYMENT);


    }

    /**
     * Cierra sesión
     */
    public void logout(){

        //Limpia las SharedPreferences almacenadas durante la sesión
        SharedPreferences prefs = this.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();



        //Vuelve a LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }


}