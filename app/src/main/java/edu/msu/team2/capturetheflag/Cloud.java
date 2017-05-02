
package edu.msu.team2.capturetheflag;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Cloud {

    private static final String CREATE_URL = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-createuser.php";
    private static final String INIT_GAME = "http://webdev.cse.msu.edu/~vasque59/cse476/project3/game-creategame.php";
    private static final String UPDATE_URL = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-updateflag.php";
    private static final String SCORE_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-scorepoint.php";
    private static final String GETSCORE_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-getpoints.php";
    private static final String DELETE_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-delete.php";
    private static final String RESET_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-resetflag.php";
    private static final String OPFLAG_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-getopponentsteamflagloc.php";
    private static final String CKRESET_URK = "https://webdev.cse.msu.edu/~vasque59/cse476/project3/game-getflagpickedup.php";
    private static final String SETPICKUP_URL =  "http://webdev.cse.msu.edu/~vasque59/cse476/project3/game-pickupflag.php";
    private static final String UTF8 = "UTF-8";

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }

    public InputStream updateFlagLoc(final String lat, final String flong,final String teamID) {
        // Create a get query
        String query = UPDATE_URL + "?flagLat=" + lat + "&flagLong=" + flong + "&teamid=" + teamID;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    public InputStream score(final String teamID) {
        // Create a get query
        String query = SCORE_URL + "?teamid=" + teamID;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream getPoints(final String teamID) {
        // Create a get query
        String query = GETSCORE_URL + "?teamid=" + teamID;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }

    }

/*       try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return -1;
            }

            InputStream streamer = conn.getInputStream();
            boolean fail = streamer == null;
            if(!fail) {
                try {
                    XmlPullParser xml = Xml.newPullParser();
                    xml.setInput(streamer, "UTF-8");


                    xml.nextTag();
                    xml.require(XmlPullParser.START_TAG, null, "game");
                    String points = xml.getAttributeValue(null, "msg");

                    return Integer.valueOf(points);




                } catch (XmlPullParserException ex) {
                    fail = true;

                }
            }

        } catch (MalformedURLException e) {
            // Should never happen
            return -1;
        } catch (IOException ex) {
            return -1;
        }
        return -1;*/
    /**
     * Open a connection to a hatting in the cloud.
     * @return reference to an input stream or null if this fails
     */
    public InputStream deleteFromCloud() {
        // Create a get query
        String query = DELETE_URL;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream resetFlag(final String lat1, final String long1,final String lat2, final String long2, final String teamID) {
        String query = RESET_URL + "?flagLat1=" + lat1 + "&flagLong1=" + long1 + "&flagLat2=" + lat2 + "&flagLong2=" + long2 + "&teamid=" + teamID;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream getOpFlag(final String teamID) {
        String query = OPFLAG_URL + "?teamid=" + teamID;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }

    }



    public InputStream initGame(final String lat1, final String long1,final String lat2, final String long2) {
        String query = INIT_GAME + "?flagLat1=" + lat1 + "&flagLong1=" + long1 + "&flagLat2=" + lat2 + "&flagLong2=" + long2;



        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream createAccount(final String user) {
        String query = CREATE_URL + "?user=" + user;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }



    public InputStream setPickUp(final String teamID) {
        String query = SETPICKUP_URL + "?teamid=" + teamID;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }

    }


    public InputStream getPickUp(final String teamID) {
        String query = CKRESET_URK + "?teamid=" + teamID;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return conn.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ex) {
            return null;
        }

    }





}