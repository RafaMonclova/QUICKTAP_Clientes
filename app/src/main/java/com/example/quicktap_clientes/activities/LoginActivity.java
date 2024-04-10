package com.example.quicktap_clientes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quicktap_clientes.adapters.MyViewPagerAdapter;
import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.fragments.IPDialogFragment;
import com.example.quicktap_clientes.fragments.LoginCallback;
import com.example.quicktap_clientes.fragments.RegisterCallback;
import com.example.quicktap_clientes.tasks.ConnectToServerTask;
import com.example.quicktap_clientes.tasks.LoginTask;
import com.example.quicktap_clientes.tasks.RegistrarTask;
import com.example.quicktap_clientes.tasks.SendMessageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;

import message.Message;

public class LoginActivity extends AppCompatActivity implements LoginCallback, RegisterCallback {


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;
    FloatingActionButton btnSettings;

    //Conexión al servidor
    public static ConnectToServerTask conexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CONEXIÓN AL SERVIDOR

        //Comprueba si es la primera vez que se inicia la aplicación, buscando en sus SharedPreferences los datos de conexión
        //y el usuario
        SharedPreferences prefs = getSharedPreferences("DatosConexion", Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);

        String ip = prefs.getString("ip","Default");
        String puerto = prefs.getString("puerto","Default");

        String usuario = prefs2.getString("usuario","Default");

        //Si hay un usuario almacenado, se limpia el usuario y se cierra el socket si hay alguna conexión existente
        //Y recarga la activity
        if(!usuario.equals("Default")){
            SharedPreferences.Editor editor2 = prefs2.edit();
            editor2.clear();
            editor2.apply();

            try {
                MainActivity.conexion.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            recreate(); //Recarga la activity


        }

        //Si no, significa que nunca se inició
        else{

            //Si hay una ip y puerto guardados, la aplicación se conecta con esos datos
            if(!ip.equals("Default") && !puerto.equals("Default")){

                Log.d("ENTRA","SHARED");

                conexion = new ConnectToServerTask(ip,Integer.parseInt(puerto),this);
                conexion.execute();
            }
            //Si no, abre el fragment para introducir esos datos
            else{

                IPDialogFragment dialogFragment = new IPDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "ip_dialog");

            }

        }


        //Componentes de la interfaz
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 =findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);
        btnSettings = findViewById(R.id.btn_settings);

        //Listener para las pestañas de Login y Registro
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IPDialogFragment dialogFragment = new IPDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "ip_dialog");

            }
        });

    }


    /**
     * Realiza el login del usuario en el servidor
     * @param email El email del usuario
     * @param passw La contraseña del usuario
     */
    public void login(String email, String passw) {

        //Crear un mensaje que contenga el usuario y la contraseña
        ArrayList<Object> data = new ArrayList<>();
        data.add(email);
        data.add(passw);

        Message mensaje = new Message("LOGIN","LOGIN_ANDROID",data);

        //Enviar el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
        new SendMessageTask(conexion.getOut()).execute(mensaje);

        //Lee la respuesta del servidor en un hilo separado, a través del flujo de entrada de la conexión
        LoginTask loginTask = new LoginTask(conexion.getIn(),this);
        loginTask.execute();

    }

    /**
     * Registra a un nuevo usuario en la BDD
     * @param usuario El nombre del usuario
     * @param email El email del usuario
     * @param passw La contraseña del usuario
     */
    public void registrar(String usuario, String email, String passw){

        ArrayList<Object> data = new ArrayList<>();
        ArrayList<String> roles = new ArrayList<>();

        //Añade rol cliente
        roles.add("cliente");

        data.add(usuario);
        data.add(email);
        data.add(passw);
        data.add(roles);

        //Enviar el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
        Message message = new Message("LOGIN","REGISTRO", data);
        new SendMessageTask(conexion.getOut()).execute(message);

        //Lee la respuesta del servidor en un hilo separado, a través del flujo de entrada de la conexión
        RegistrarTask registrarTask = new RegistrarTask(conexion.getIn(),this);
        registrarTask.execute();

    }

    /**
     * Inicia la actividad del mapa si el login es correcto
     * @param mensaje Mensaje de información
     * @param usuario Nombre del usuario logeado
     */
    @Override
    public void onLoginSuccess(String mensaje, String usuario) {
        Toast.makeText(LoginActivity.this, mensaje +" "+ usuario + "!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("usuario",usuario);
        startActivity(intent);
    }

    /**
     * Se lanza si hay un error en el login
     * @param mensaje Mensaje de error
     */
    @Override
    public void onLoginFailure(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    /**
     * Mensaje que informa del éxito al registrar un nuevo usuario
     * @param mensaje Mensaje de información
     * @param usuario El nombre del usuario registrado
     */
    @Override
    public void onRegisterSuccess(String mensaje, String usuario) {
        Toast.makeText(LoginActivity.this, mensaje +" "+ usuario + "!", Toast.LENGTH_SHORT).show();

    }

    /**
     * Mensaje que informa de un error al registrar al nuevo usuario
     * @param mensaje Mensaje de error
     */
    @Override
    public void onRegisterFailure(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }
}