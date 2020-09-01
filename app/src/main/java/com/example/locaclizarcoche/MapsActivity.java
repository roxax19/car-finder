package com.example.locaclizarcoche;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button botonVovler, botonMaps;

    Intent intent;

    Location location;
    String nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Mapeamos
        botonVovler = (Button) findViewById(R.id.botonVolver);
        botonMaps = (Button) findViewById(R.id.botonMaps);

        //Obtenemos la ubicación
        intent = getIntent();
        location = intent.getParcelableExtra("location");
        nombre = intent.getStringExtra("nombre");


        //Establecemos el comportamiento del boton
        botonVovler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        botonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creamos la uri
                //Por algun motivo no funciona lo del nombre
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+location.getLatitude()+","+location.getLongitude()+"("+nombre+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }else{
                    Toast.makeText(MapsActivity.this, "No se ha encontrado aplicación para abrir el enlace", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker and move the camera
        LatLng latlon = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlon).title("Desconexión con "+nombre));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, mMap.getMaxZoomLevel()-2));
        Log.d("ZOOM",Float.toString(mMap.getMaxZoomLevel()));
    }
}
