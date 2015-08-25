package com.edmond.jimi.util;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by apple on 15/8/11.
 */
public class ConnectUtil {
    public static JSONObject getJsonFromUrl() {
        URL url;
        URLConnection urlConnection;
        InputStream in = null;
        try {
            url = new URL("ftp://mirror.csclub.uwaterloo.ca/index.html");

            urlConnection = url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());
            //should read from inputstream
        } catch (IOException ioe) {

        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
