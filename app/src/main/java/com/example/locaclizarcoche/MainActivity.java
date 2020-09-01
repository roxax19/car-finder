package com.example.locaclizarcoche;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.locaclizarcoche.db.ConnSQLiteHelper;
import com.example.locaclizarcoche.db.Ubicacion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_ON;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_ENABLE_BLUETOOTH= 1;
    public static int REQUEST_ACCESS_LOCATION = 2;

    BluetoothAdapter btAdapter;
    List<Ubicacion> ubicacionesList ;

    RecyclerView recyclerUbicaciones;
    AdapterRecyclerView adapterRecyclerUbicaciones;
    RecyclerView.LayoutManager layoutManager;

    Switch switchBT;
    Button boton;

    BluetoothDevice lastDeviceDisconnected;

    private FusedLocationProviderClient fusedLocationClient;

    IntentFilter filter = new IntentFilter();

    //Definimos un BroadcastListener
    final BluetoothReceiver mReceiver = new BluetoothReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //Vemos si la accion es un cambio de estado
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        switchBT.setChecked(false);
                        break;
                    case STATE_ON:
                        switchBT.setChecked(true);
                        break;
                }
            }

            //Vemos si la accion es un cambio de estado en la conexión
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED);
                if(state == BluetoothAdapter.STATE_DISCONNECTED || state == BluetoothAdapter.STATE_DISCONNECTING){
                    //Guardo el dispositivo que se ha desconectado
                    lastDeviceDisconnected = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(MainActivity.this, "Desconectado de: "+ lastDeviceDisconnected.getName(), Toast.LENGTH_SHORT).show();

                    //Solicito ubicación y la guardo
                    obtenerUltimaUbicacion();
                }
            }



        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Enlazamos los elementos
        switchBT = (Switch) findViewById(R.id.switchBT);
        recyclerUbicaciones = (RecyclerView) findViewById(R.id.recyclerUbicaciones);
/*
        boton = (Button) findViewById(R.id.boton);
*/
        //Inicializo las listas de dispositivos
        ubicacionesList = new ArrayList<Ubicacion>();

        //Obtenemos el adaptador bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            //El teléfono no soporta BT
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int
                                        which) {
                                    System.exit(0);
                                }
                            })
                    .show();
        }else {
            //Continuamos con la aplicacion

            //Solicitamos los permisos de ubicacion. Distintos según la version de android
            int androidVersion = Build.VERSION.SDK_INT;
            if (androidVersion >= 23 && androidVersion <29){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        }, REQUEST_ACCESS_LOCATION);
            }else if(androidVersion >= 29){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        }, REQUEST_ACCESS_LOCATION);
            }

            //Comprobamos si el BT esta activo
            comprobarBTActivo();

            //Inicializamos el localizador
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            //Inicamos los recyclers
            inicializarReciclers();

/*            //Establecemos el comportamiento del boton
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    registrarUbicacion();
                    actualizarRecyclerView();

                }
            });*/

            //Establecemos el comportamiento del switch
            switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        //Paramos busqueda
                        btAdapter.cancelDiscovery();
                        //Desactivamos BT
                        btAdapter.disable();

                        Toast.makeText(MainActivity.this, "El bluetooth se ha desactivado", Toast.LENGTH_SHORT).show();

                    }else{
                        //Intent para solicitar encenderlo
                        Intent enableBT = new
                                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBT, REQUEST_ENABLE_BLUETOOTH);
                    }

                }
            });

            //Instanciamos el BroadcastListener
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);


        }
    }

    private void inicializarReciclers() {
        //Asignamos la vista
        recyclerUbicaciones = (RecyclerView) findViewById(R.id.recyclerUbicaciones);

        //Asignamos el layoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerUbicaciones.setLayoutManager(layoutManager);
/*        //Separador de elementos
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerUbicaciones.getContext(), DividerItemDecoration.VERTICAL);
        recyclerUbicaciones.addItemDecoration(dividerItemDecoration);*/

        //Iniciamos adapter
        adapterRecyclerUbicaciones = new AdapterRecyclerView(MainActivity.this, ubicacionesList);

        //Actualizamos y asignamos adapter
        actualizarRecyclerView();

    }

    private void actualizarRecyclerView() {

        //Actuazliamos la ubicacionesList
        actualizarUbicacionesList();

        //Actualizamos el recycler view
        adapterRecyclerUbicaciones = new AdapterRecyclerView(MainActivity.this, ubicacionesList);
        recyclerUbicaciones.setAdapter(adapterRecyclerUbicaciones);
        recyclerUbicaciones.setLayoutManager(layoutManager);
        adapterRecyclerUbicaciones.notifyDataSetChanged();
    }

    private void actualizarUbicacionesList(){

        ubicacionesList = new ArrayList<Ubicacion>();
        Ubicacion ubicacion;

        //consultamos la base de datos
        ConnSQLiteHelper conn = new ConnSQLiteHelper(this, "test_db", null, 1);

        Cursor cursor = conn.queryUbicacion();

        while(cursor.moveToNext()){

            //recogemos la entrada
            ubicacion = new Ubicacion();
            ubicacion.set_id(cursor.getInt(0));
            ubicacion.setNombre(cursor.getString(1));
            ubicacion.setLat(cursor.getString(2));
            ubicacion.setLon(cursor.getString(3));
            ubicacion.setFecha(cursor.getString(4));

            ubicacionesList.add(ubicacion);
        }


    }

    private void registrarUbicacion() {
        //utilizado para anadir ubicacions rapidamente para pruebas
        ConnSQLiteHelper conn = new ConnSQLiteHelper(this, "test_db", null, 1);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format( new Date()   );

        Ubicacion ubicacion1 = new Ubicacion();
        Ubicacion ubicacion2 = new Ubicacion();

        ubicacion1.setNombre("Jaen");
        ubicacion1.setLat("37.7692200");
        ubicacion1.setLon("-3.7902800");
        ubicacion1.setFecha(dateString);

        ubicacion2.setNombre("ETSIT UMA");
        ubicacion2.setLat("36.7151845");
        ubicacion2.setLon("-4.4775607");
        ubicacion2.setFecha(dateString);

        conn.addUbicacion(ubicacion1);
        conn.addUbicacion(ubicacion2);



        Toast.makeText(MainActivity.this, "Añadidos", Toast.LENGTH_SHORT).show();


    }

    private void obtenerUltimaUbicacion(){

        //Solicitamos la última localización
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //Creo una nueva Ubicacion
                            Ubicacion ubicacion = new Ubicacion();

                            ubicacion.setNombre(lastDeviceDisconnected.getName());
                            ubicacion.setLat(Double.toString(location.getLatitude()));
                            ubicacion.setLon(Double.toString(location.getLongitude()));

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateString = format.format( new Date()   );
                            ubicacion.setFecha(dateString);

                            //La añado a la base de datos
                            ConnSQLiteHelper conn = new ConnSQLiteHelper(MainActivity.this, "test_db", null, 1);
                            conn.addOrUpdateUbicacion(ubicacion);

                            //Actualizamos los recyclers
                            actualizarRecyclerView();


                        }
                    }
                });
    }

    private void comprobarBTActivo() {
        if (!btAdapter.isEnabled()) {
            //Si no esta activo desactivamos el switch
            switchBT.setChecked(false);

        }else{
            //Si esta activo, activamos el switch
            switchBT.setChecked(true);
        }
    }

}

/*Tareas:
*  - implementar boton ubicación (NO SE PUEDE POR TEMAS DE PRIVACIDAD)
*/