package edu.bloomu.mlasko.scavengeit;

import androidx.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * The Point class used to store the location, picture, and hint for a scavenger hunt
 * point of interest utilizing the Realm database framework.  The points have a unique
 * primary key used to allow easy access to these points in the game.
 *
 * @author Michael J Lasko
 */
public class Points extends RealmObject {
    @PrimaryKey
    @NonNull
    private int pointNumber;
    private byte[] bitMap;
    private double latitude;
    private double longitude;
    private String hint;


    /**
     * Getter for the id of the current hunt point that is used as the primary key in the
     * Realm database.  This is not utilized but still necessary with Realm to utilize
     * some powerful methods of access to the database.
     */
    public int getPointNumber() { return pointNumber; }

    /**
     * Setter for the id of the current hunt point that is used as the primary key in the
     * Realm database.
     *
     * @return The id of the point.
     */
    public void setPointNumber(int pointNumber) { this.pointNumber = pointNumber; }

    /**
     * Getter for the byte array containing the bitmap picture of the current hunt point
     * that is used as the primary key in the Realm database.
     *
     * @return A byte array containing a bitmap picture of the point.
     */
    public byte[] getBitMap() {
        return bitMap;
    }

    /**
     * Setter for the byte array containing the bitmap picture of the current hunt point
     * that is used as the primary key in the Realm database.
     */
    public void setBitMap(byte[] bitMap) {
        this.bitMap = bitMap;
    }

    /**
     * Getter for the latitude of the location where the current hunt point that is used
     * as the primary key in the Realm database is found.
     *
     * @return The latitude location of the point.
     */
    public double getLatitude() { return this.latitude; }

    /**
     * Setter for the latitude of the location where the current hunt point that is used
     * as the primary key in the Realm database is found.
     */
    public void setLatitude(double latitude) { this.latitude = latitude; }

    /**
     * Getter for the longitude of the location where the current hunt point that is used
     * as the primary key in the Realm database is found.
     *
     * @return The longitude location of the point.
     */
    public double getLongitude() { return this.longitude; }

    /**
     * Setter for the longitude of the location where the current hunt point that is used
     * as the primary key in the Realm database is found.
     */
    public void setLongitude(double longitude) { this.longitude = longitude; }

    /**
     * Getter for the hint provided by the person setting up the game.
     *
     * @return The hint for the point
     */
    public String getHint() { return hint; }

    /**
     * Setter for the hint provided by the person setting up the game.
     */
    public void setHint(String hint) { this.hint = hint; }
}

