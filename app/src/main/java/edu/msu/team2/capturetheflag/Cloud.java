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

public class Cloud {

    private static final String CREATE_URL = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-create.php";
    private static final String INIT_GAME = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-init.php";
    private static final String SAVE_URL = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-save.php";
    private static final String WIN_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-win.php";
    private static final String DELETE_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-delete.php";
    private static final String HARD_DELETE_URL  = "https://webdev.cse.msu.edu/~vasque59/cse476/project1/game-hard-delete.php";
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


    public InputStream winGame(final String user, final String gameid) {
        // Create a get query
        String query = WIN_URL + "?gameid=" + gameid + "&user=" + user;

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


    /**
     * Open a connection to a hatting in the cloud.
     * @return reference to an input stream or null if this fails
     */
    public InputStream deleteFromCloud(final String game, final String userid) {
        // Create a get query
        String query = DELETE_URL + "?gameid=" + game + "&user=" + userid;

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

    public boolean save(final String xmlStr, final String gameid) {
        /*
         * Convert the XML into HTTP POST data
         */
        String postDataStr;
        try {
            postDataStr = "xml=" + URLEncoder.encode(xmlStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return false;
        }


        /*
         * Send the data to the server
         */
        byte[] postData = postDataStr.getBytes();

        InputStream stream = null;
        // Create a get query
        String query = SAVE_URL + "?gameid=" + gameid;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("hatter", line);
            }
            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xmlR = Xml.newPullParser();
                xmlR.setInput(stream, UTF8);

                xmlR.nextTag();      // Advance to first tag
                xmlR.require(XmlPullParser.START_TAG, null, "game");

                String status = xmlR.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }




        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;

    }

    public InputStream hardDelete(final String game) {
        String query = HARD_DELETE_URL + "?gameid=" + game;

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


    public InputStream initGame(final String game) {
        String query = INIT_GAME + "?gameid=" + game;

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

    public InputStream createAccount(final String user, final String pw, final String pw2) {
        String query = CREATE_URL + "?user=" + user + "&pw=" + pw + "&pw2=" + pw2;

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
