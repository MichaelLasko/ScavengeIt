package edu.bloomu.mlasko.scavengeit;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This activity is created after the planner takes a photograph of a point to use in the
 * Scavenger hunt.  It prompts the planner to enter a hint for the player to utilize in
 * finding this location.
 *
 * @author Michael J Lasko
 */
public class AddDetailsActivity extends AppCompatActivity {
    private byte[] imageByte;
    private ImageView imageView;
    private Points point;
    private EditText inputField;

    /**
     * Sets up and creates the Add Detail Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        Realm realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_add_details);
        point = realm.where(Points.class).findAll().last();
        imageByte = point.getBitMap();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView = findViewById(R.id.newImage);
        imageView.setImageBitmap(bitmap);
        inputField = findViewById(R.id.hintString);
    }

    /**
     * Takes the hint string supplied by the planner and adds the information to the Realm
     * database.
     */
    public void addPoint(View view) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        point.setHint(inputField.getText().toString());
        realm.insertOrUpdate(point);
        realm.commitTransaction();
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }
}
