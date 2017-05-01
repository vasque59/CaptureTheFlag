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
    private boolean delivered = false;
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
    public void setDelivered(boolean d){
        delivered = d;
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

    public boolean isDelivered() {
        return delivered;
    }
    public void reset(){
        latitude = originalLatitude;
        longitude = originalLongitude;
    }
    public void setCarriedBy(String name){
        carriedBy = name;
    }
    public String getCarriedBy(){
        return carriedBy;
    }

}
