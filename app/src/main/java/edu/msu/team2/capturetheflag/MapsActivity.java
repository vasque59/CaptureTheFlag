package edu.msu.team2.capturetheflag;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    double latitude;
    double longitude;
    boolean carryFlag;
    boolean start = false;
    Cloud cloud = new Cloud();
    int myTeam;
    String My_name;
    public TextView message;
    public Flag carrying;
    public int score;

    private GoogleMap mMap;
    private LocationManager locationManager = null;
    private MarkerOptions blue_flag_1 = new MarkerOptions();
    private MarkerOptions red_flag_1 = new MarkerOptions();
    private MarkerOptions player = new MarkerOptions();
    private Marker blue_flag_marker;
    private Marker red_flag_marker;
    private Marker player_marker;
    private ArrayList<Flag> blueFlags = new ArrayList<>();
    private ArrayList<Flag> redFlags = new ArrayList<>();
    /**
     * images
     * final
     * using larger resource and scale the bitmap to make it display more clear.
     * **/
    private Bitmap resizedRedPerson;
    private Bitmap resizedBluePerson;
    private Bitmap resizedRedHome;
    private Bitmap resizedBlueHome;
    private Bitmap resizedRedFlag;
    private Bitmap resizedBlueFlag;



    private final ActiveListener activeListener = new ActiveListener();

    private class ActiveListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            registerListeners();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Force the screen to say on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        carryFlag = false;
        message = (TextView) findViewById(R.id.Message_label);
        savedInstanceState = getIntent().getExtras();
        if(savedInstanceState != null)
            My_name = getIntent().getStringExtra("name");
        /**
         * Add images
         * Resize those image to make it display accurate and clear.
         * **/
        Bitmap redPerson = BitmapFactory.decodeResource(getResources(), R.drawable.redp);
        resizedRedPerson = Bitmap.createScaledBitmap(redPerson, 32, 32, false);
        Bitmap bluePerson = BitmapFactory.decodeResource(getResources(), R.drawable.bluep);
        resizedBluePerson = Bitmap.createScaledBitmap(bluePerson, 32, 32, false);
        Bitmap redHome = BitmapFactory.decodeResource(getResources(), R.drawable.redhome);
        resizedRedHome = Bitmap.createScaledBitmap(redHome, 64, 64, false);
        Bitmap blueHome = BitmapFactory.decodeResource(getResources(), R.drawable.bluehome);
        resizedBlueHome = Bitmap.createScaledBitmap(blueHome, 64, 64, false);
        Bitmap redFlag = BitmapFactory.decodeResource(getResources(), R.drawable.redflag);
        resizedRedFlag = Bitmap.createScaledBitmap(redFlag, 64, 64, false);
        Bitmap blueFlag = BitmapFactory.decodeResource(getResources(), R.drawable.blueflag);
        resizedBlueFlag = Bitmap.createScaledBitmap(blueFlag, 64, 64, false);
        myTeam  = 0;//Integer.valueOf(getIntent().getStringExtra("teamID"));


        /**
         *
         * Assign the team based on server-side response.
         *
         * get Names from cloud/last activity.
         * **/
        /*
        if(){
            myTeam = 0; // blue
        }
        else{
            myTeam = 1; // red
        }
        */
        if(myTeam == 1)
            message.setText(getResources().getString(R.string.hi) + My_name + getResources().getString(R.string.redTeam));
        else
            message.setText(getResources().getString(R.string.hi) + My_name + getResources().getString(R.string.blueTeam));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        registerListeners();

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 14.0f) );
        blue_flag_marker = addFlag(blue_flag_1,42.734182, -84.482822,0);//MSU Union
        red_flag_marker = addFlag(red_flag_1,42.721028, -84.488552,1);//Holden Hall

        blueFlags.add(new Flag(42.734182, -84.482822));
        redFlags.add(new Flag(42.721028, -84.488552));

        start = true;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(42.724934, -84.481098));// EB, Red base
        markerOptions.title(getResources().getString(R.string.Red_base));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedRedHome));
        mMap.addMarker(markerOptions);

        markerOptions.position(new LatLng(42.731491, -84.495263));// Brody, Blue base
        markerOptions.title(getResources().getString(R.string.Blue_base));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBlueHome));
        mMap.addMarker(markerOptions);

    }

    private void registerListeners() {
        unregisterListeners();
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        String bestAvailable = locationManager.getBestProvider(criteria, true);
        if (bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                }
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }


    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }

    private void onLocation(Location location) {
        if(location == null) {
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        updateLocation();
    }
    private void updateLocation(){
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 14.0f) );
        if(start) {
            updateFlags();
            detectWin();
        }
        if(!carryFlag) {
            if(player_marker != null) {
                player_marker.remove();
            }
            player_marker = addFlag(player,latitude, longitude, myTeam + 2);
            // show only to yourself your current location
        }
        else{
            if(player_marker != null) {
                player_marker.remove();
            }
            player_marker = addFlag(player,latitude, longitude, myTeam);
            carrying.setLatitude(latitude);
            carrying.setLongitude(longitude);
            cloud.updateFagLoc(String.valueOf(latitude),String.valueOf(longitude),String.valueOf(myTeam));
            //If you carry the flag, your position shows to everybody.
        }
    }


    private void updateFlags(){
        if(myTeam == 0){  //blue
            if(calculateDistanceInMeter(latitude,longitude,blueFlags.get(0).getLatitude(),blueFlags.get(0).getLongitude()) < 120 && !blueFlags.get(0).isCarried() && !blueFlags.get(0).isDelivered() && !carryFlag){
                carryFlag = true;
                blueFlags.get(0).setCarried(true);
                blueFlags.get(0).setCarriedBy(My_name);
                carrying =  blueFlags.get(0);
                if(player_marker != null)
                    player_marker.remove();
                if(blue_flag_marker != null){
                    blue_flag_marker.remove();
                }
                player_marker = addFlag(player,latitude, longitude, myTeam);
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.pick));
            }
                else if(calculateDistanceInMeter(latitude,longitude,redFlags.get(0).getLatitude(),redFlags.get(0).getLongitude()) < 120 && redFlags.get(0).isCarried() && !redFlags.get(0).isDelivered()){
                    redFlags.get(0).reset();
                    if(player_marker != null)
                        player_marker.remove();
                    player_marker = addFlag(player,latitude, longitude, myTeam + 2);
                    if(red_flag_marker != null){
                        red_flag_marker.remove();
                        red_flag_marker = addFlag(red_flag_1,42.721028, -84.488552,0);//holden
                    }
                    message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.reset));
                    //Notify opponent's flag has been reset. Change back his flag icon.
                }
                // blue deliver a flag
                else if(calculateDistanceInMeter(latitude,longitude,42.731491, -84.495263) < 120 && carryFlag && blueFlags.get(0).isCarried() && !blueFlags.get(0).isDelivered()){
                    carryFlag = false;
                    blueFlags.get(0).setCarried(false);
                    blueFlags.get(0).setDelivered(true);
                    if(player_marker != null) {
                        player_marker.remove();
                    }
                    player_marker = addFlag(player,latitude, longitude, myTeam + 2);
                    score ++;
                    blue_flag_marker = addFlag(blue_flag_1,42.734182, -84.482822,myTeam);//union
                    blueFlags.get(0).setCarried(false);
                    blueFlags.get(0).setDelivered(false);
                    blueFlags.get(0).setCarriedBy(null);
                    carryFlag = false;
                    carrying = null;
                    blueFlags.get(0).setLatitude(42.734182);
                    blueFlags.get(0).setLongitude(-84.482822);
                    message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.deliver));
                }
            }
            //close to blue flag, pick it up
            //close to red flag, reset it back
        else{ // you are red
                if(calculateDistanceInMeter(latitude,longitude,redFlags.get(0).getLatitude(),redFlags.get(0).getLongitude()) < 120 && !redFlags.get(0).isCarried() && !redFlags.get(0).isDelivered() && !carryFlag){
                    carryFlag = true;
                    redFlags.get(0).setCarried(true);
                    redFlags.get(0).setCarriedBy(My_name);
                    carrying = redFlags.get(0);
                    if(player_marker != null)
                        player_marker.remove();
                    if(red_flag_marker != null) {
                        red_flag_marker.remove();
                    }
                    player_marker = addFlag(player,latitude, longitude, myTeam);
                    message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.pick));
                }
                else if(calculateDistanceInMeter(latitude,longitude,blueFlags.get(0).getLatitude(),blueFlags.get(0).getLongitude()) < 120 && blueFlags.get(0).isCarried() && !blueFlags.get(0).isDelivered()){
                    blueFlags.get(0).reset();
                    //Notify opponent's flag has been reset. Change back his flag icon.
                    if(player_marker != null)
                        player_marker.remove();
                    player_marker = addFlag(player,latitude, longitude, myTeam + 2);
                    if(blue_flag_marker != null){
                        blue_flag_marker.remove();
                        blue_flag_marker = addFlag(blue_flag_1,42.734182, -84.482822,0);//MSU Union
                    }
                    message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.reset));
                }
                else if(calculateDistanceInMeter(latitude,longitude,42.724934, -84.481098) < 120 && carryFlag && redFlags.get(0).isCarried() && !redFlags.get(0).isDelivered()){
                    carryFlag = false;
                    redFlags.get(0).setCarried(false);
                    redFlags.get(0).setDelivered(true);
                    if(player_marker != null) {
                        player_marker.remove();
                    }
                    player_marker = addFlag(player,latitude, longitude, myTeam + 2);
                    score ++;
                    redFlags.get(0).setCarried(false);
                    redFlags.get(0).setDelivered(false);
                    redFlags.get(0).setCarriedBy(null);
                    carryFlag = false;
                    carrying = null;
                    red_flag_marker = addFlag(red_flag_1,42.721028, -84.488552,myTeam);//holden
                    redFlags.get(0).setLatitude(42.721028);
                    redFlags.get(0).setLongitude(-84.488552);
                    message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.deliver));
                }
            }
            //close to red flag, pick it up
            //close to blue flag, reset it back


    }


    private void detectWin(){
        if(myTeam == 0 && score == 3){
            message.setText(getResources().getString(R.string.blueWin));
            unregisterListeners();
        }
        else if(myTeam == 1 && score == 3){
            message.setText(getResources().getString(R.string.redWin));
            unregisterListeners();
        }
    }
    /**
     * color = 0,1,2,or 3
     * 0 means blue flag,
     * 1 means red flag,
     * 2 means blue team player
     * 3 means red team player
     * **/
    private Marker addFlag(MarkerOptions flag, double la, double lon, int color){
        flag.position(new LatLng(la,lon));
        if(color == 0) {
            flag.title(getResources().getString(R.string.blueFlag));
            flag.icon(BitmapDescriptorFactory.fromBitmap(resizedBlueFlag));
        }
        else if(color == 1){
            flag.title(getResources().getString(R.string.redFlag));
            flag.icon(BitmapDescriptorFactory.fromBitmap(resizedRedFlag));
        }
        else if(color == 2){
            flag.title(getResources().getString(R.string.yourself));
            flag.icon(BitmapDescriptorFactory.fromBitmap(resizedBluePerson));
        }
        else if(color == 3){
            flag.title(getResources().getString(R.string.yourself));
            flag.icon(BitmapDescriptorFactory.fromBitmap(resizedRedPerson));
        }
        Marker m = mMap.addMarker(flag);
        return m;
    }

    @Override
    public void onBackPressed(){

    }
    public void onBtnSurrender(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.surrender_btn);
        builder.setMessage(R.string.quit_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                unregisterListeners();
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }
    public void onBtnYour_pos(View view) {
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 14.0f) );
    }
    public void onBtnBase_pos(View view) {
        if(myTeam == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.731491, -84.495263), 14.0f));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.724934, -84.481098), 14.0f));
        }
    }

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private double calculateDistanceInMeter(double lat, double lon, double tolat, double tolon) {

        double latDistance = Math.toRadians(lat - tolat);
        double lonDistance = Math.toRadians(lon - tolon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(tolat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (AVERAGE_RADIUS_OF_EARTH_KM * c) * 1000;
    }

}
