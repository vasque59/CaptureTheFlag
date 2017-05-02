package edu.msu.team2.capturetheflag;

/**
 * Created by a1 on 17/4/29.
 */

public class Flag {
    private double latitude;
    private double longitude;
    private double originalLatitude;
    private double originalLongitude;
    private boolean carried = false;
    private boolean reset = true;
    private String carriedBy;

    public Flag(double la, double lo){
        latitude = la;
        longitude = lo;
        originalLatitude = la;
        originalLongitude = lo;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setCarried(boolean c){
        carried = c;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCarried() {
        return carried;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean b){
        reset = b;
    }
    public void reset(){
        latitude = originalLatitude;
        longitude = originalLongitude;
        reset = true;
    }
    public void setCarriedBy(String name){
        carriedBy = name;
    }
    public String getCarriedBy(){
        return carriedBy;
    }

    public double getOriginalLatitude(){
        return originalLatitude;
    }
    public double getOriginalLongitude(){
        return originalLongitude;
    }
}
