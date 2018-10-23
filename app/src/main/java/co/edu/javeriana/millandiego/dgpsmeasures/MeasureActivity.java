package co.edu.javeriana.millandiego.dgpsmeasures;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeasureActivity extends AppCompatActivity {

    public static final String TAG = "LOCATION_APP";
    public static final int ID_PERMISSION_LOCATION = 1;
    public static final String reason = "Permission required to access location";

    private TextView latitude, longitude, elevation, distance_to_airport;
    private FusedLocationProviderClient location_client;
    private Button refresh_location;
    private ListView log_list_view;
    private ArrayList<LocationLog> log_list;
    private LocationRequest mlocation_request;
    private LocationCallback mlocation_callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // initialize location provider client
        location_client = LocationServices.getFusedLocationProviderClient(this);
        // find relevant views
        latitude = (TextView) findViewById(R.id.lat_val);
        longitude = findViewById(R.id.lng_val);
        elevation = findViewById(R.id.ele_val);
        distance_to_airport = findViewById(R.id.dist_val);
        refresh_location = findViewById(R.id.refresh_button);
        log_list_view = findViewById(R.id.location_log);
        log_list = new ArrayList<LocationLog>();
        mlocation_request = createLocationRequest();

        // define location update callback
        mlocation_callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.i(TAG, "longitude: " + location.getLongitude());
                    Log.i(TAG, "latitude: " + location.getLatitude());
                    String dateString = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy").format(new Date(location.getTime()));
                    String formatted_log = String.format("Lat: %.8f - Long: %.8f on %s", location.getLatitude(), location.getLongitude(),dateString);
                    log_list.add(new LocationLog(formatted_log));
                    double distance = distance(4.702065, location.getLatitude(),-74.144443, location.getLongitude());

                    updateViews(String.valueOf(
                            location.getLatitude()),
                            String.valueOf(location.getLongitude()),
                            String.valueOf(location.getAltitude()),
                            String.valueOf(distance)
                    );
                }
            }
        };


        // ask for location permission
        requestPermission(this,Manifest.permission.ACCESS_FINE_LOCATION,reason,ID_PERMISSION_LOCATION);
        requestLocation();

        refresh_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });


    }

    private void updateViews(String lat, String lon, String elev, String distance) {
        latitude.setText(lat);
        longitude.setText(lon);
        elevation.setText(elev);
        distance_to_airport.setText(distance);
        // must set the adapter
        LocationLogAdapter adapter = new LocationLogAdapter(this, R.layout.location_row_layout,log_list);
        log_list_view.setAdapter(adapter);

    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if(ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)){
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    private void requestLocation() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            location_client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.i(TAG, "Location fetched!");
                    if (location != null) {
                        Log.i(TAG, "longitude: " + location.getLongitude());
                        Log.i(TAG, "latitude: " + location.getLatitude());
                        String dateString = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy").format(new Date(location.getTime()));
                        String formatted_log = String.format("Lat: %.8f - Long: %.8f on %s", location.getLatitude(), location.getLongitude(),dateString);
                        log_list.add(new LocationLog(formatted_log));
                        double distance = distance(4.702065, location.getLatitude(),-74.144443, location.getLongitude());

                        updateViews(String.valueOf(
                                location.getLatitude()),
                                String.valueOf(location.getLongitude()),
                                String.valueOf(location.getAltitude()),
                                String.valueOf(distance)
                        );
                    }
                }
            });
        }
    }

    // override method to check the permission status
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case ID_PERMISSION_LOCATION : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission for location granted", Toast.LENGTH_LONG).show();
                    requestLocation();
                } else {
                    Toast.makeText(this, "Permission for location not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }

    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(600000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void startLocationUpdates() {
        //Verificación de permiso!!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location_client.requestLocationUpdates(mlocation_request, mlocation_callback, null);
        }
    }


}
