package com.example.quicktap_clientes.activities;

import static com.example.quicktap_clientes.activities.LoginActivity.conexion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicktap_clientes.R;
import com.example.quicktap_clientes.tasks.EstablecimientosTask;
import com.example.quicktap_clientes.tasks.SendMessageTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import message.Message;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener {

    String usuario; //Usuario logeado
    String establecimiento; //Establecimiento seleccionado

    //Elementos de la interfaz
    private Button btn_buscarEstabl;
    private Button btn_permisos_ubicacion;
    private TextView tv_radio;

    private GoogleMap googleMap;
    private MapView mapView;
    private SeekBar seekBar;
    private Circle searchCircle;

    //Permiso de ubicación
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    //Obtiene los datos de ubicación del dispositivo
    private LocationManager locationManager;

    //Almacena las coordenadas del dispositivo
    LatLng latLng;

    //Marcadores activos
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        usuario = (String) intent.getExtras().get("usuario");

        btn_buscarEstabl = findViewById(R.id.btn_buscarEstabl);
        btn_buscarEstabl.setEnabled(false);

        btn_permisos_ubicacion = findViewById(R.id.btn_permisos_ubicacion);

        btn_buscarEstabl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEstablecimientosCercanos();

            }
        });

        btn_permisos_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLocationPermission();

            }
        });

        //Inicializa el MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        tv_radio = findViewById(R.id.tv_radio);

        //Inicializa la SeekBar
        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);

        //Verifica los permisos de ubicación al iniciar la actividad
        checkLocationPermission();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv_radio.setText("Radio: "+progress/1000 + "km");
                dibujarRadio();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    /**
     * Obtiene la ubicación actual en el LocationManager
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);

        } else {
            showGPSDisabledDialog();
        }
    }

    /**
     * Muestra un dialog si el GPS está desactivado
     */
    private void showGPSDisabledDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("El GPS está desactivado. ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Pregunta al usuario por los permisos de ubicación
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);

        } else {
            getLocation();
            btn_permisos_ubicacion.setText("Permisos concedidos");
            btn_permisos_ubicacion.setEnabled(false);
            seekBar.setEnabled(true);
            btn_buscarEstabl.setEnabled(true);
        }
    }

    /**
     * Verifica los permisos dados por el usuario. Si se dan permisos, desbloquea los elementos de la interfaz
     * @param requestCode Código de solicitud
     * @param permissions Permisos que se dan
     * @param grantResults Resultados de la operación
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
                onMapReady(googleMap);
                btn_permisos_ubicacion.setText("Permisos concedidos");
                btn_permisos_ubicacion.setEnabled(false);
                seekBar.setEnabled(true);
                btn_buscarEstabl.setEnabled(true);

            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Escucha cambios en la localización del LocationManager. Si hay un cambio, la actualiza
     * @param location La nueva localización
     */
    @Override
    public void onLocationChanged(Location location) {
        locationButton();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Carga el mapa de Google Maps con el botón de ubicación actual y marcadores
     * @param map Mapa de Google Maps
     */
    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnInfoWindowClickListener(this);


        //Maneja el clic en el botón de ubicación
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //Obtiene la ubicación actual
                locationButton();
                return false;
            }
        });


    }

    /**
     * Obtiene la ubicación actual y la guarda en la variable de clase
     */
    public void locationButton(){
        //Obtiene la ubicación actual
        Location location = googleMap.getMyLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng coords = new LatLng(latitude,longitude);
            latLng = coords;

        }
    }

    /**
     * Obtiene la lista de establecimientos de la BDD dentro del radio dado por el usuario
     */
    private void getEstablecimientosCercanos() {

        if (searchCircle != null) {

            double radius = searchCircle.getRadius();

            ArrayList<Object> data = new ArrayList<>();
            data.add(latLng.latitude);
            data.add(latLng.longitude);
            data.add(radius);

            Message mensaje = new Message("ESTABLECIMIENTO", "GET_ESTABLECIMIENTOS_CERCANOS", data);

            //Envia el mensaje al servidor en un hilo separado, a través del flujo de salida de la conexión
            new SendMessageTask(conexion.getOut()).execute(mensaje);

            //Carga en la lista de establecimientos, los establecimientos recibidos del servidor
            EstablecimientosTask establecimientosTask = new EstablecimientosTask(conexion.getIn(), this);
            establecimientosTask.execute();


        }
    }

    /**
     * Añade los marcadores a los establecimientos recibidos
     * @param listaEstablecimientos La lista de establecimientos
     */
    public void añadirMarcadores(ArrayList<Object> listaEstablecimientos) {
        googleMap.clear();

        dibujarRadio(); //Dibuja el radio sobre el mapa

        if (listaEstablecimientos != null) {
            for (Object restaurant : listaEstablecimientos) {
                ArrayList<Object> camposEstabl = (ArrayList<Object>) restaurant;

                //Obtiene las coordenadas de cada establecimiento de la lista
                LatLng latLng = new LatLng((Double) camposEstabl.get(2), (Double) camposEstabl.get(3));

                //Dibuja el marcador en el mapa, con el nombre del establecimiento y la dirección
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title((String) camposEstabl.get(0))
                        .snippet((String) camposEstabl.get(1));
                Marker marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();
                googleMap.addMarker(markerOptions);
                markerList.add(marker);

            }
        }
    }

    /**
     * Dibuja el radio dado por el usuario en el mapa, y lo va actualizando
     */
    public void dibujarRadio(){
        if (searchCircle != null) {
            searchCircle.remove();
        }


        searchCircle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(seekBar.getProgress()) //Multiplica por 1000 para convertir a metros
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3")));
    }

    /**
     * Escucha el evento de click sobre la ventana de información del marcador
     * @param marker El marcador del mapa
     */
    @Override
    public void onInfoWindowClick(Marker marker) {

        //Datos del marcador
        String title = marker.getTitle();
        String description = marker.getSnippet();


        establecimiento = title;

        //Inicia la MainActivity con el usuario actual y el establecimiento elegidio
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("establecimiento",establecimiento);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }


}