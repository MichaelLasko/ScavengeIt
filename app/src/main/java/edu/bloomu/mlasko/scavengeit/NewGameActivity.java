package edu.bloomu.mlasko.scavengeit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.ByteArrayOutputStream;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * The New Game Activity class allows the planner to add locations they want the player
 * to find.  A camera activity is called upon when the planner wants to document the
 * item to find and a GPS location is then also assigned to the point.  This activity
 * displays the currently collected points and asks the planner to either add more or
 * finish for the present time.
 *
 * @author Michael J Lasko
 */
public class NewGameActivity extends AppCompatActivity implements LocationListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 95;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;
    protected LocationManager locationManager;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LocationCallback mLocationCallback;
    private Realm realm;

    /**
     * Creates the New Game Activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        // For hiding the Action Bar and making the Activity fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_game);

        // Setting up the location requests and checking for user permission to use GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewGameActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        // Client to be used to update the location regularly
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // The frequency and sensitivity of the location requests.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            /**
             * Inner Method setting up the regular location requests
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {

                    Location location = locationList.get(locationList.size() - 1);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    lastLocation = location;
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback, Looper.myLooper());
    }


    /**
     * When paused, the location updates should stop.
     */
    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Resumes the activity and sets up the Activity to display recently gathered points.
     */
    @Override
    protected void onResume() {
        super.onResume();
        createPointList();
    }

    /**
     * called after a photo is taken by the camera.  This sets up the information to store
     * in a Realm type object and proceeds to store this.  Calls the Add Details Activity
     * to ask the user to supply a hint
     *
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Points point = new Points();
            point.setPointNumber(((MyApplication) this.getApplication()).getCurrentGameNumber());
            ((MyApplication) this.getApplication()).incrementGameNumber();
            realm.beginTransaction();
            point.setLatitude(latitude);
            point.setLongitude(longitude);
            point.setBitMap(byteArray);
            realm.insertOrUpdate(point);
            realm.commitTransaction();
            Intent intent = new Intent(this, AddDetailsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Required method to maintain location updates
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    /**
     * Required for when the location provider is not accessible.
     */
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disabled");
    }

    /**
     * Required for when the location provider is accessible.
     */
    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enabled");
    }

    /**
     * Called when the status of the location provider changes.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status changed");
    }

    /**
     * if a certain button is pressed, this sends a picture taking intent.
     */
    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * If a certain button is pressed, this sends an intent to return to the previous
     * screen
     */
    public void finishAdding(View view) {
        Intent intent = new Intent(this, SelectionScreenActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Once permission is requested from the user to access their location, this method
     * establishes that connection
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }

            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Generates an image list view on the activity to display points previously entered.
     */
    public void createPointList() {
        Realm realm = Realm.getDefaultInstance();
        LinearLayout linear = findViewById(R.id.grid_points);

        RealmResults<Points> result = realm.where(Points.class).findAll();
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                Points point = result.get(i);
                ImageView imageView = new ImageView(this);
                byte[] imageByte = point.getBitMap();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imageView.setImageBitmap(bitmap);
                imageView.setMinimumWidth(375);
                imageView.setMinimumHeight(500);
                imageView.setPadding(0,10, 0, 10);
                linear.addView(imageView);
            }
        }
    }
}
