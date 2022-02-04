package edu.bloomu.mlasko.scavengeit;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Top level Application activity used to hold global variables and set up the Realm to be
 * used by all activities that required it.
 *
 * @author Michael J Lasko
 */
public class MyApplication extends Application {
    private int currentGameNumber = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm" +
                ".realm").build();
        Realm.setDefaultConfiguration(config);

    }

    /**
     * Provides the value of the game number global variable.
     *
     * @return the current game number.
     */
    public int getCurrentGameNumber() { return currentGameNumber; }

    /**
     * Increments the current game number
     */
    public void incrementGameNumber() {
        this.currentGameNumber++;
    }
}
