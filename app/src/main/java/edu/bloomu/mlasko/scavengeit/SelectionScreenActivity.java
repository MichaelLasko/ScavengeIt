package edu.bloomu.mlasko.scavengeit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

/**
 * Main activity for the Scavenge It app which displays the logo and allows the user to
 * either select to create a game, modify an already created game, or play a new game
 * that has already been created.
 *
 * @author Michael J Lasko
 */
public class SelectionScreenActivity extends AppCompatActivity {
    private Realm realm;

    /**
     * Creates the Selection Screen Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hides the title bar and enables fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_selection_screen);
        realm = Realm.getDefaultInstance();
    }

    /**
     * Starts an activity that sets up a new game from scratch.
     */
    public void createGame(View view) {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Modifies an already existing game or, if an existing game doesn't exist, creates a
     * new one from scratch.
     */
    public void modifyGame(View view) {
        if (!realm.isEmpty()) {
            Intent intent = new Intent(this, NewGameActivity.class);
            startActivity(intent);
            finish();
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "No game created!  Please create a " +
                    "new game first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 200);
            toast.show();
        }
    }

    /**
     * Starts a new game by shifting over to the Google map / GPS activity.
     */
    public void startGame(View view) {
        realm = Realm.getDefaultInstance();
        if(!realm.isEmpty()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "No game created!  Please create a " +
                    "new game first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 200);
            toast.show();
        }
    }
}
