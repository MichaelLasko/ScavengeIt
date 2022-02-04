package edu.bloomu.mlasko.scavengeit;

import android.content.Intent;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.realm.Realm;

/**
 * This activity displays the overall results of the hunt to the user.
 *
 * @author Michael J Lasko
 */
public class EndActivity extends AppCompatActivity {
    private Realm realm;
    private Intent intent, resultIntent;
    private Bundle extras;
    private double totalDistance;
    private int index;
    private TextView view;

    /**
     * Creates this End Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hides the title bar and enables fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_end);

        resultIntent = getIntent();
        extras = resultIntent.getExtras();
        totalDistance = extras.getDouble("total");
        index = extras.getInt("index");

        realm = Realm.getDefaultInstance();
        view = findViewById(R.id.game_statistics1);
        view.setText("You were a total distance of " + totalDistance + " feet away from" +
                " your " + index +" hunted items!!!");
        view = findViewById(R.id.game_statistics2);
        view.setText("You were an average of " + round(totalDistance / index) +
                " away from each of the hunted items!  Good Job!!!");

    }

    /**
     * Allows the user to return to the title page to play again or set up a new game.
     */
    public void returnBeginning(View view) {
        intent = new Intent(this, SelectionScreenActivity.class);
        startActivity(intent);
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
