
package edu.bloomu.mlasko.scavengeit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This class provides the user feedback on the points they have entered up till this
 * point.  It displays how close the user was to the current hunt point as well as the
 * total distance they have been away from all points up until now.
 *
 * @author Michael J Lasko
 */
public class ResultScreenActivity extends AppCompatActivity {
    private Intent intent, returnIntent;
    private Bundle extras;
    private Realm realm;
    private double distance, totalSoFar;
    private int index;
    private Points p;
    private TextView text;
    private ImageView image;

    /**
     * Sets this activity up for use when initially created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        realm = Realm.getDefaultInstance();
        RealmResults<Points> result = realm.where(Points.class).findAll();
        intent = getIntent();
        returnIntent = new Intent(this, MapsActivity.class);
        extras = intent.getExtras();
        distance = extras.getDouble("distance");
        totalSoFar = extras.getDouble("total");
        totalSoFar += distance;
        index = extras.getInt("index");
        p = result.get(0);
        byte[] imageByte = p.getBitMap();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        image = findViewById(R.id.current_point);
        image.setImageBitmap(bitmap);
        text = findViewById(R.id.distance_away);
        String pointDistance =
                "You were " + round(distance) +" feet away from hunt item " + (index + 1);
        text.setText(pointDistance);
        text = findViewById(R.id.total_distance);
        String totalDistance =
                "Total distance away from all hunt items: " + round(totalSoFar);
        text.setText(totalDistance);
        index++;
    }

    /**
     * Sends an intent containing the index number incremented by one and the total
     * distance away from all hunt items entered up until now.
     */
    public void returnEntry(View view) {
        returnIntent.putExtra("total", round(totalSoFar));
        returnIntent.putExtra("index", index);
        startActivity(returnIntent);
    }

    /**
     * Rounds the distances to 2 significant digits past the decimal point
     * @param value The value to be rounded
     * @return A decimal in the format #.##
     */
    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
