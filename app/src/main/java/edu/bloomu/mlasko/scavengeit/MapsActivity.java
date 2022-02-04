package edu.bloomu.mlasko.scavengeit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This class sets up a Google Map interface and monitors the device's built in GPS to
 * provide current location information in the form of latitude and longitude to the user.
 * It has two buttons to allow the user to check the objectives of the scavenger hunt and
 * another to input the coordinate he/she thinks the planner was at when taking the photo.
 *
 * @author Michael J Lasko
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Bundle extras;
    private Realm realm;
    private Intent intent;
    private RealmResults<Points> result;
    private int zoom = 20, index = 0;
    private double longitude, latitude;
    private double totalSoFar;
    private int pointIndex;
    private SupportMapFragment frag;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 98;

    /**
     * Creates the Map Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
        result = realm.where(Points.class).findAll();
        intent = getIntent();
        totalSoFar = intent.getDoubleExtra("total", 0);
        pointIndex = intent.getIntExtra("index", pointIndex);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        frag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        frag.getMapAsync(this);
        // This is for when all the points have been entered by the user.
        if(pointIndex >= result.size()) {
            extras = new Bundle();
            intent = new Intent(this, EndActivity.class);
            extras.putDouble("total", totalSoFar);
            extras.putInt("index", pointIndex);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    /**
     * Removes the location update when this activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Sets up the location information to be used by Google Maps and sets up the map
     * object to be used in conjunction with this location information.
     *
     * @param googleMap A Google Map object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.requestLocationUpdates(locationRequest,
                        mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                checkLocationPermission();
            }
        }
        else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {

                Location location = locationList.get(locationList.size() - 1);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                TextView textView = findViewById(R.id.location);
                textView.setText("Lat: " + latitude + "\nLong: " + longitude);

                lastLocation = location;


                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        }
    };

    /**
     * Verifies the User has allowed location services to be used by this activity.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    /**
     * If the user has provided the Map Activity permission to use location services, this
     * method sets up the recurrent location checking.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Responds to a button press to allow the user to check the Scavenger list
     * objectives.
     */
    public void scavengerList(View view) {
        intent = new Intent(this, ScavengerListActivity.class);
        intent.putExtra("total", totalSoFar);
        intent.putExtra("index", pointIndex);
        startActivity(intent);
    }

    /**
     * Responds to a button press to allow the user to enter a hunt location they have
     * potentially found.C
     */
    public void enterAnswers(View view) {
        intent = new Intent(this, EnterResultsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("total", totalSoFar);
        intent.putExtra("index", pointIndex);
        startActivity(intent);
    }
}
