package com.example.locaclizarcoche;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.locaclizarcoche.db.ConnSQLiteHelper;
import com.example.locaclizarcoche.db.Ubicacion;

import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.MyViewHolder> {
    //Creamos un listener para cada objeto
    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device);
    }

    private Context mCtx;
    private List<Ubicacion> ubicacionList;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nombreDispositivo, fechaDispositivo;
        Button botonMostrarUbicacion, botonEliminarUbicacion;

        public MyViewHolder(View itemView) {
            super(itemView);
            //enlazamos
            nombreDispositivo = itemView.findViewById(R.id.itemNombreDispositivo);
            fechaDispositivo = itemView.findViewById(R.id.itemFechaDispositivo);
            botonMostrarUbicacion = (Button) itemView.findViewById(R.id.botonMostrarUbicacion);
            botonEliminarUbicacion = (Button) itemView.findViewById(R.id.botonEliminarUbicacion);


        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerView(Context mCtx, List<Ubicacion> UbicaiconList) {
        this.ubicacionList = UbicaiconList;
        this.mCtx = mCtx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Ubicacion ubicacion = ubicacionList.get(position);

        holder.nombreDispositivo.setText(ubicacion.getNombre());
        holder.fechaDispositivo.setText(ubicacion.getFecha());

        holder.botonMostrarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Construir un objeto location desde la lat y la lon
                Location targetLocation = new Location("");
                targetLocation.setLatitude(Double.parseDouble(ubicacion.getLat()));
                targetLocation.setLongitude(Double.parseDouble(ubicacion.getLon()));

                Log.d("LOCATION", "lat: "+targetLocation.getLatitude()+" lon: "+targetLocation.getLongitude());


                //Nos vamos a la actividad de mostrar ubicacion y pasamos targetLocation
                Intent intent = new Intent(mCtx, MapsActivity.class);
                intent.putExtra("location", targetLocation);
                intent.putExtra("nombre", ubicacion.getNombre());
                mCtx.startActivity(intent);
            }
        });

        holder.botonEliminarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnSQLiteHelper conn = new ConnSQLiteHelper(mCtx, "test_db", null, 1);
                conn.deleteUbicacion(ubicacion);

                ubicacionList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), ubicacionList.size());

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ubicacionList.size();
    }

}

