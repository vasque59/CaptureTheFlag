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
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
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
    public int blue_score;
    public int red_score;
    public double red_lat;
    public double red_long;
    public double blue_lat;
    public double blue_long;

    public boolean blue_pick;
    public boolean red_pick;

    private GoogleMap mMap;
    private LocationManager locationManager = null;

    private Marker blue_flag_marker;
    private Marker red_flag_marker;
    private Marker player_marker;

    private Flag blueFlag;
    private Flag redFlag;

    /**
     * images
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
        myTeam = Integer.valueOf(getIntent().getStringExtra("teamID"));
        score = 0;


        /**
         *
         * Assign the team based on server-side response.
         *
         * get Names from cloud/last activity.
         * **/

        if(myTeam == 2)
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
        registerListeners();

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 14.0f) );
        blue_flag_marker = addFlag(42.734182, -84.482822,0);//MSU Union
        red_flag_marker = addFlag(42.721028, -84.488552,1);//Holden Hall

        blueFlag = new Flag(42.734182, -84.482822);
        redFlag = new Flag(42.721028, -84.488552);

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
            drawFlags();
            detectWin();
        }

        if(!carryFlag) {
            if(player_marker != null) {
                player_marker.remove();
            }
            player_marker = addFlag(latitude, longitude, myTeam + 1); //player
            // show only to yourself your current location
        }

        if(carryFlag && myTeam == 1 && blue_pick) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cloud.updateFlagLoc(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(myTeam));
                }
            }).start();

            if (player_marker != null) {
                player_marker.remove();
            }
            if (myTeam == 1) {
                blueFlag.setLatitude(latitude);
                blueFlag.setLongitude(longitude);
            } else if (myTeam == 2) {
                redFlag.setLatitude(latitude);
                redFlag.setLongitude(longitude);
            }
        }

        else if(carryFlag && myTeam == 2 && red_pick){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cloud.updateFlagLoc(String.valueOf(latitude),String.valueOf(longitude),String.valueOf(myTeam));
                }
            }).start();

            if(player_marker != null) {
                player_marker.remove();
            }
            if(myTeam == 1){
                blueFlag.setLatitude(latitude);
                blueFlag.setLongitude(longitude);
            }
            else if(myTeam == 2){
                redFlag.setLatitude(latitude);
                redFlag.setLongitude(longitude);
            }
        }
    }

    private void updateFlags(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = cloud.getPickUp(String.valueOf(1));

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");

                        String temp = xml.getAttributeValue(null,"msg");
                        if(temp.equals("0")){
                            blue_pick = false;
                        }
                        else if(temp.equals("1")){
                            blue_pick = true;
                        }
                    } catch (IOException | XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

            }

        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = cloud.getPickUp(String.valueOf(2));

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");

                        String temp = xml.getAttributeValue(null,"msg");
                        if(temp.equals("0")){
                            red_pick = false;
                        }
                        else if(temp.equals("1")){
                            red_pick = true;
                        }
                    } catch (IOException | XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

            }

        }).start();




        if(myTeam == 1){  //blue

            if(calculateDistanceInMeter(latitude,longitude,blueFlag.getLatitude(),blueFlag.getLongitude()) < 120 && !blueFlag.isCarried() && blueFlag.isReset() && !carryFlag && !blue_pick){
                carryFlag = true;
                blueFlag.setReset(false);
                blueFlag.setCarried(true);
                blueFlag.setCarriedBy(My_name);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.setPickUp(String.valueOf(1));
                    }

                }).start();

                carrying =  blueFlag;
                if(player_marker != null)
                    player_marker.remove();
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.pick));
                blueFlag.setLatitude(latitude);
                blueFlag.setLongitude(longitude);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(latitude),String.valueOf(longitude),String.valueOf(myTeam));
                    }

                }).start();

            }
            //reset red
            else if(calculateDistanceInMeter(latitude,longitude,redFlag.getLatitude(),redFlag.getLongitude()) < 120 && red_pick){
                redFlag.reset();
                if(player_marker != null)
                    player_marker.remove();
                player_marker = addFlag(latitude, longitude, myTeam + 1);
                if(red_flag_marker != null) {
                    red_flag_marker.remove();
                }
                if(red_flag_marker != null) {
                    red_flag_marker.remove();
                }
                red_flag_marker = addFlag(42.721028, -84.488552,1);//holden
                redFlag.setLatitude(42.721028);
                redFlag.setLongitude(-84.488552);
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.reset));


                /**Todo
                 ** This reset clear the isPickedUp status but instantly set back to pickedUp. Don't know why.
                 **/


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.resetFlag(String.valueOf(latitude), String.valueOf(longitude), "42.721028", "-84.488552", String.valueOf(1));
                    }

                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(42.721028),String.valueOf(-84.488552),String.valueOf(2));
                    }

                }).start();




                //something need to be add to notify the carrier that he got reset.

                //Notify opponent's flag has been reset. Change back his flag icon.
            }

            // blue deliver a flag
            else if(calculateDistanceInMeter(latitude,longitude,42.731491, -84.495263) < 120 && carryFlag && blueFlag.isCarried() && !blueFlag.isReset()){
                carryFlag = false;
                blueFlag.setCarried(false);
                if(player_marker != null) {
                    player_marker.remove();
                }
                player_marker = addFlag(latitude, longitude, myTeam + 1); // draw a person rather than a flag
                //score ++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.score(String.valueOf(myTeam));
                    }

                }).start();

                if(blue_flag_marker != null) {
                    blue_flag_marker.remove();
                }
                blue_flag_marker = addFlag(42.734182, -84.482822,myTeam - 1);//union
                blueFlag.setCarried(false);
                blueFlag.setCarriedBy(null);
                blueFlag.reset();
                carryFlag = false;
                carrying = null;
                //blueFlag.setLatitude(42.734182);
                //blueFlag.setLongitude(-84.482822);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(42.734182),String.valueOf(-84.482822),String.valueOf(1));
                    }

                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.resetFlag(String.valueOf(latitude), String.valueOf(longitude), "42.734182", "-84.482822", String.valueOf(2));
                    }

                }).start();


                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.deliver) + "\n Your team has " + score + " points, " + String.valueOf(3 - score) + "more points to win");
                //blue team delivered a flag, reset flag, score ++
            }
        }
        //close to blue flag, pick it up
        //close to red flag, reset it back
        else { // you are red

            if(calculateDistanceInMeter(latitude,longitude,redFlag.getLatitude(),redFlag.getLongitude()) < 120 && !redFlag.isCarried() && redFlag.isReset() && !carryFlag && !red_pick){
                carryFlag = true;
                redFlag.setCarried(true);
                redFlag.setReset(false);
                redFlag.setCarriedBy(My_name);
                carrying =  redFlag;
                if(player_marker != null)
                    player_marker.remove();
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.pick));
                redFlag.setLatitude(latitude);
                redFlag.setLongitude(longitude);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.setPickUp(String.valueOf(2));
                    }

                }).start();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(latitude),String.valueOf(longitude),String.valueOf(myTeam));
                    }

                }).start();

            }
            //reset blue
            else if(calculateDistanceInMeter(latitude,longitude,blueFlag.getLatitude(),blueFlag.getLongitude()) < 120 && blue_pick){
                blueFlag.reset();
                if(player_marker != null)
                    player_marker.remove();
                player_marker = addFlag(latitude, longitude, myTeam + 1);

                if(blue_flag_marker != null) {
                    blue_flag_marker.remove();
                }
                blue_flag_marker = addFlag(42.734182, -84.482822,0);//MSU Union

                //blueFlag.setLatitude(42.734182);//
                //blueFlag.setLongitude(-84.482822);
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.reset));


                /**Todo
                 ** This reset clear the isPickedUp status but instantly set back to pickedUp. Don't know why.
                 **/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.resetFlag(String.valueOf(latitude), String.valueOf(longitude), "42.734182", "-84.482822", String.valueOf(2));
                    }

                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(42.734182),String.valueOf(-84.482822),String.valueOf(1));
                    }

                }).start();



                //something need to be add to notify the carrier that he got reset.

                //Notify opponent's flag has been reset. Change back his flag icon.
            }
            // red deliver a flag
            else if(calculateDistanceInMeter(latitude,longitude,42.724934, -84.481098) < 120 && carryFlag && redFlag.isCarried() && !redFlag.isReset()){
                carryFlag = false;
                redFlag.setCarried(false);
                if(player_marker != null) {
                    player_marker.remove();
                }
                player_marker = addFlag(latitude, longitude, myTeam + 1); // draw a person rather than a flag
                //score ++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.score(String.valueOf(myTeam));
                    }

                }).start();

                if(red_flag_marker != null)
                    red_flag_marker.remove();
                red_flag_marker = addFlag(42.721028, -84.488552,myTeam-1);//Holden Hall
                redFlag.setCarried(false);
                redFlag.setCarriedBy(null);
                carryFlag = false;
                carrying = null;
                redFlag.reset();
                //redFlag.setLatitude(42.721028);
                //redFlag.setLongitude(-84.488552);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.updateFlagLoc(String.valueOf(42.721028),String.valueOf(-84.488552),String.valueOf(2));
                    }

                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.resetFlag(String.valueOf(latitude), String.valueOf(longitude), "42.721028", "-84.488552", String.valueOf(1));
                    }

                }).start();
                message.setText(getResources().getString(R.string.cong)+ My_name + getResources().getString(R.string.deliver) + "\n Your team has " + score + " points, " + String.valueOf(3 - score) + "more points to win");
                //red team delivered a flag, reset flag, score ++
            }
        }
        //close to red flag, pick it up
        //close to blue flag, reset it back

    }


    private void detectWin(){
        if(blue_score == 3){
            message.setText(getResources().getString(R.string.blueWin));
            unregisterListeners();
        }
        else if(red_score == 3){
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
    private Marker addFlag(double la, double lon, int color){
        MarkerOptions flag = new MarkerOptions();
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloud.deleteFromCloud();
                    }

                }).start();
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

    private void drawFlags(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                InputStream stream = cloud.getPoints(String.valueOf(1));

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");

                        String temp = xml.getAttributeValue(null,"msg");
                        blue_score = Integer.valueOf(temp);
                    } catch (IOException | XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

            }

        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                InputStream stream = cloud.getPoints(String.valueOf(2));

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");

                        String temp = xml.getAttributeValue(null,"msg");
                        red_score = Integer.valueOf(temp);
                    } catch (IOException | XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

            }

        }).start();

        // pulling points;
        if(myTeam == 1 && score != blue_score){
            score = blue_score;

            message.setText("Your team just get one point! Now  you have " + String.valueOf(score) + " points." );
        }
        if(myTeam == 2 && score != red_score){
            score = red_score;
            message.setText("Your team just get one point! Now  you have " + String.valueOf(score) + " points." );
        }



        if(myTeam == 1) {//1 blue
            //final int temp_score;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    InputStream stream = cloud.getOpFlag("1");

                    boolean fail = stream == null;
                    if (!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");


                            xml.nextTag();
                            xml.require(XmlPullParser.START_TAG, null, "game");

                            red_lat = Double.valueOf(xml.getAttributeValue(null,"lat"));
                            red_long = Double.valueOf(xml.getAttributeValue(null,"long"));

                        } catch (IOException | XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                            }
                        }
                    }

                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    InputStream stream = cloud.getOpFlag("2");

                    boolean fail = stream == null;
                    if (!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");


                            xml.nextTag();
                            xml.require(XmlPullParser.START_TAG, null, "game");

                            blue_lat = Double.valueOf(xml.getAttributeValue(null,"lat"));
                            blue_long = Double.valueOf(xml.getAttributeValue(null,"long"));

                        } catch (IOException | XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                            }
                        }
                    }

                }

            }).start();



            blueFlag.setLatitude(blue_lat);
            blueFlag.setLongitude(blue_long);
            redFlag.setLatitude(red_lat);
            redFlag.setLongitude(red_long);
            if(red_flag_marker != null){
                red_flag_marker.remove();
                red_flag_marker = addFlag(red_lat,red_long,1);
            }
            else if(red_flag_marker == null){
                red_flag_marker = addFlag(42.721028, -84.488552,1);
            }

            if(blue_flag_marker != null){
                blue_flag_marker.remove();
            }
            blue_flag_marker = addFlag(blueFlag.getLatitude(),blueFlag.getLongitude(),0);
        }
        if(myTeam == 2) {//2 red

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    InputStream stream = cloud.getOpFlag("2");

                    boolean fail = stream == null;
                    if (!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");


                            xml.nextTag();
                            xml.require(XmlPullParser.START_TAG, null, "game");

                            blue_lat = Double.valueOf(xml.getAttributeValue(null,"lat"));
                            blue_long = Double.valueOf(xml.getAttributeValue(null,"long"));

                        } catch (IOException | XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                            }
                        }
                    }

                }

            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    InputStream stream = cloud.getOpFlag("1");

                    boolean fail = stream == null;
                    if (!fail) {
                        try {
                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");


                            xml.nextTag();
                            xml.require(XmlPullParser.START_TAG, null, "game");

                            red_lat = Double.valueOf(xml.getAttributeValue(null,"lat"));
                            red_long = Double.valueOf(xml.getAttributeValue(null,"long"));

                        } catch (IOException | XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                            }
                        }
                    }

                }
            }).start();

            redFlag.setLatitude(red_lat);
            redFlag.setLongitude(red_long);
            blueFlag.setLatitude(blue_lat);
            blueFlag.setLongitude(blue_long);
            if(red_flag_marker != null){
                red_flag_marker.remove();
            }
            red_flag_marker = addFlag(redFlag.getLatitude(),redFlag.getLongitude(),1);
            if(blue_flag_marker != null){
                blue_flag_marker.remove();
                blue_flag_marker = addFlag(blue_lat,blue_long,0);
            }
            else if(blue_flag_marker == null){
                blue_flag_marker = addFlag(42.734182, -84.482822,0);
            }
        }
    }

}