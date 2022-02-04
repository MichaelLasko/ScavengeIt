package edu.bloomu.mlasko.scavengeit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This Activity class allows the user to enter the latitude and longitude of the point
 * the planner photographed.  It then directs the user to the Result Screen to see the
 * proximity to where the photo was taken and the total distance of all points entered
 * thus far.
 *
 * @author Michael J Lasko
 */
public class EnterResultsActivity extends AppCompatActivity {
    private Points p;
    private EditText inputLatitude, inputLongitude;
    private Realm realm;
    private Intent intent, resultIntent;
    private RealmResults<Points> result;
    private int index = 0;
    private double totalSoFar = 0;
    private double latitude, longitude;
    private Bundle extras;


    /**
     * Creates the Enter Results Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results);
        // Getting the results back from the Result Screen shown to the user after
        // submitting points in this activity.
        resultIntent = getIntent();
        latitude = resultIntent.getDoubleExtra("latitude", 0);
        longitude = resultIntent.getDoubleExtra("longitude", 0);
        totalSoFar = resultIntent.getDoubleExtra("total", 0.0);
        index = resultIntent.getIntExtra("index", index);
        realm = Realm.getDefaultInstance();
        extras = new Bundle();
        result = realm.where(Points.class).findAll();
        LinearLayout layout = findViewById(R.id.enter_results);
        p = result.get(index);
        ImageView image = new ImageView(this);
        byte[] imageByte = p.getBitMap();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        image.setImageBitmap(bitmap);
        image.setMinimumWidth(500);
        image.setMinimumHeight(700);
        image.setPadding(0, 10, 0, 10);
        layout.addView(image);
        TextView text = new TextView(this);
        text.setText(p.getHint());
        text.setPadding(0, 0, 0, 30);
        text.setTextSize(20);
        text.setTextColor(getResources().getColor(R.color.white, null));
        text.setGravity(Gravity.CENTER);
        layout.addView(text);
        String lat = "" + latitude;
        String lng = "" + longitude;
        inputLatitude = findViewById(R.id.enter_latitude);
        inputLongitude = findViewById(R.id.enter_longitude);
        inputLatitude.setText(lat, TextView.BufferType.NORMAL);
        inputLongitude.setText(lng, TextView.BufferType.NORMAL);
    }

    /**
     * Upon button press, this method submits the answer types in by the user for the
     * latitude and longitude of the hunt point.
     */
    public void submitAnswers(View view) {
        double lat = Double.parseDouble(inputLatitude.getText().toString());
        double lng = Double.parseDouble(inputLongitude.getText().toString());
        double distance = haversineFormula(lat, lng, p.getLatitude(), p.getLongitude());
        intent = new Intent(this, ResultScreenActivity.class);
        extras.putDouble("distance", distance);
        extras.putDouble("total", totalSoFar);
        extras.putInt("index", index);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    /**
     * The Haversine Formula is one of the most accurate equations used to get the
     * distance between two locations based on their latitude and longitude.
     *
     * @param latEnter The latitude of the point entered by the user.
     *
     * @param longEnter The longitude of the point entered by the user.
     *
     * @param latItem The latitude of where the photograph was taken of the point by
     *                the planner.
     *
     * @param longItem The longitude of where the photograph was taken of the point by
     *      *                the planner.
     *
     * @return The distance (in feet) between these two points.
     */
    public static double haversineFormula(double latEnter, double longEnter,
                                          double latItem, double longItem) {
        double radiusOfEarth = 6372.8;
        double diffLat = Math.toRadians(latItem - latEnter);
        double diffLon = Math.toRadians(longItem - longEnter);
        double temp = Math.sin(diffLat / 2) * Math.sin(diffLat / 2) +
                Math.cos(Math.toRadians(latEnter)) * Math.cos(Math.toRadians(latItem))
                        * Math.sin(diffLon / 2) * Math.sin(diffLon / 2);
        double temp2 = 2 * Math.asin(Math.sqrt(temp));
        return radiusOfEarth * temp2 * 3280.84;
    }

    /**
     * Returns the user to the Google Map / GPS so they could continue playing the game.
     */
    public void backToMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("total", totalSoFar);
        intent.putExtra("index", index);
        startActivity(intent);
    }
}
