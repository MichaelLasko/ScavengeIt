package edu.bloomu.mlasko.scavengeit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This Activity Class is used by the user as a reference to see which scavenger hunt
 * locations they need to track down as well as a hint to aid the user.
 *
 * @author Michael J Lasko
 */
public class ScavengerListActivity extends AppCompatActivity {
    private Intent intent;
    private double totalSoFar;
    private int index;

    /**
     * Creates the Scavenger List Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_list);
        intent = getIntent();
        totalSoFar = intent.getDoubleExtra("total", 0.0);
        index = intent.getIntExtra("index", 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Points> result = realm.where(Points.class).findAll();
        LinearLayout layout = findViewById(R.id.scavenger_list);
        for (Points p: result) {
            ImageView image = new ImageView(this);
            byte[] imageByte = p.getBitMap();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            image.setImageBitmap(bitmap);
            image.setMinimumWidth(500);
            image.setMinimumHeight(700);
            image.setPadding(0,10, 0, 10);
            layout.addView(image);
            TextView text = new TextView(this);
            text.setText(p.getHint());
            text.setPadding(0,0,0, 30);
            text.setTextSize(20);
            text.setTextColor(getResources().getColor(R.color.white,null));
            text.setGravity(Gravity.CENTER);
            layout.addView(text);
        }
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
