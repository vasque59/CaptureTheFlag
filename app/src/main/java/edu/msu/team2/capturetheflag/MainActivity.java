package edu.msu.team2.capturetheflag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public String MyName;

    private String teamID;

    private String team1lat  = "42.734182";
    private String team1long  = "-84.482822";//MSU Union

    private String team2lat  = "42.730864";
    private String team2long  = "-84.483202";//MSU Library

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private EditText getUsernameEditText() {
        return (EditText)findViewById(R.id.usernameEditText);
    }

    public void onOkay(final View view) {

        final String username = getUsernameEditText().getText().toString();

        //Log.i("on okay",username);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                InputStream stream = cloud.createAccount(username);

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");


                        String status = xml.getAttributeValue(null, "status");
                        if (status.equals("created user")) {
                            teamID = xml.getAttributeValue(null, "msg");
                            if(teamID == null){
                                teamID = "1";
                            }
                            // gameid = xml.getAttributeValue(null, "id");
                        } else {
                            fail = true;
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

                final boolean fail1 = fail;
                view.post(new Runnable() {

                    @Override
                    public void run() {
                        if (fail1) {
                            Toast.makeText(view.getContext(),
                                    R.string.loginfail,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            StartGame(view, username, teamID);
                        }
                    }
                });

            }

        }).start();


    }




    public void onCancel(View view) {
        onBackPressed();
    }

    public void StartGame(final View view, String username, String teamID) {


        final Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", username);
        intent.putExtra("teamID", teamID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                InputStream stream = cloud.initGame(team1lat, team1long, team2lat, team2long);

                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");


                        xml.nextTag();
                        xml.require(XmlPullParser.START_TAG, null, "game");
                        String status = xml.getAttributeValue(null, "status");
                        if (status.equals("game created")) {

                            fail = false;
                            //stream.close();
                            // teamID = xml.getAttributeValue(null, "msg");
                            // gameid = xml.getAttributeValue(null, "id");
                        } else if (status.equals("game exists")){
                            fail = false;
                        }
                        else{
                            fail = true;
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

                final boolean fail1 = fail;
                view.post(new Runnable() {

                    @Override
                    public void run() {
                        if (fail1) {
                            Toast.makeText(view.getContext(),
                                    "unable to create game",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // will probably pass coordinates through intent at some point
                            startActivity(intent);

                        }
                    }
                });

            }

        }).start();


    }
}

